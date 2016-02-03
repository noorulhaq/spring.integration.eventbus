package org.springframework.integration.eventbus;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.messaging.Message;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.UncheckedExecutionException;

import static com.google.common.base.Preconditions.checkArgument;


public class EventSubscriberMethodSelectorUtil {
	
	
	private EventSubscriberMethodSelectorUtil() {}
	
	protected static void selectSubcriberMethodAndInvoke(final Message<?> message,final Object subscriber){
		
		try{
			Map<Class<?>, Method> subscriberMethods = findAllSubscriberMethods(subscriber);
			Object event = message.getPayload();
			ImmutableSet<Class<?>> eventTypes = flattenHierarchy(event.getClass());
			Method subscriberMethod = null;

			for (Class<?> eventType : eventTypes) {
				subscriberMethod = subscriberMethods.get(eventType);
				if (subscriberMethod != null)
					break;
			}

			if (subscriberMethod != null) {
				subscriberMethod.invoke(subscriber, event);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
    protected static void loadSubscribeMethodsIntoCache(Object listener){
    	Class<?> clazz = listener.getClass();
    	getAnnotatedMethods(clazz);
    }

   private static Map<Class<?>, Method> findAllSubscriberMethods(Object listener) {
	 Map<Class<?>, Method> subscriberMethods = Maps.newHashMap();
     Class<?> clazz = listener.getClass();
     for (Method method : getAnnotatedMethods(clazz)) {
       Class<?>[] parameterTypes = method.getParameterTypes();
       Class<?> eventType = parameterTypes[0];
       subscriberMethods.put(eventType, method);
     }
     return subscriberMethods;
   }
    
    private static ImmutableList<Method> getAnnotatedMethods(Class<?> clazz) {
        return subscriberMethodsCache.getUnchecked(clazz);
    }
    
    private static final LoadingCache<Class<?>, ImmutableList<Method>> subscriberMethodsCache =
      CacheBuilder.newBuilder()
          .build(new CacheLoader<Class<?>, ImmutableList<Method>>() {
            @Override
            public ImmutableList<Method> load(Class<?> concreteClass) throws Exception {
              return getAnnotatedMethodsNotCached(concreteClass);
            }
     });

    private static ImmutableList<Method> getAnnotatedMethodsNotCached(Class<?> clazz) {
        Set<? extends Class<?>> supertypes = TypeToken.of(clazz).getTypes().rawTypes();
        Map<SubscriberMethodIdentifier, Method> identifiers = Maps.newHashMap();
        for (Class<?> supertype : supertypes) {
          for (Method method : supertype.getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubscribeEvent.class) && !method.isSynthetic()) {
              
            	Class<?>[] parameterTypes = method.getParameterTypes();
              
              checkArgument(parameterTypes.length == 1,
                  "Method %s annotated with @SubscribeEvent annotation has %s parameters."
                      + "@SubscribeEvent annotated methods can only have exactly one parameter.",
                  method, parameterTypes.length);
              
              // Load event types hierarchy into cache
              flattenHierarchy(parameterTypes[0]);
              
              SubscriberMethodIdentifier ident = new SubscriberMethodIdentifier(method);
              if (!identifiers.containsKey(ident)) {
                identifiers.put(ident, method);
              }
            }
          }
        }
        return ImmutableList.copyOf(identifiers.values());
      }
 
    
    private static final LoadingCache<Class<?>, ImmutableSet<Class<?>>> flattenHierarchyCache =
        CacheBuilder.newBuilder()
            //.weakKeys()
            .build(new CacheLoader<Class<?>, ImmutableSet<Class<?>>>() {
              @Override
              public ImmutableSet<Class<?>> load(Class<?> concreteClass) {
                return ImmutableSet.<Class<?>>copyOf(
                    TypeToken.of(concreteClass).getTypes().rawTypes());
              }
            });


    private static ImmutableSet<Class<?>> flattenHierarchy(Class<?> concreteClass) {
      try {
        return flattenHierarchyCache.getUnchecked(concreteClass);
      } catch (UncheckedExecutionException e) {
        throw Throwables.propagate(e.getCause());
      }
    }  
    
    private static final class SubscriberMethodIdentifier {

        private final String name;
        private final List<Class<?>> parameterTypes;

        SubscriberMethodIdentifier(Method method) {
          this.name = method.getName();
          this.parameterTypes = Arrays.asList(method.getParameterTypes());
        }

        @Override
        public int hashCode() {
          return Objects.hashCode(name, parameterTypes);
        }

        @Override
        public boolean equals(Object o) {
          if (o instanceof SubscriberMethodIdentifier) {
            SubscriberMethodIdentifier ident = (SubscriberMethodIdentifier) o;
            return name.equals(ident.name) && parameterTypes.equals(ident.parameterTypes);
          }
          return false;
        }
      }    
}
