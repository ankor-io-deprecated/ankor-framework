/*
 * Copyright (C) 2013-2014  Irian Solutions  (http://www.irian.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.irian.ankor.viewmodel.factory;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.reflect.ConstructorUtils;

import java.lang.reflect.Constructor;

/**
 * Convenient helper class that supports constructor matching and invocation.
 * Main feature of this helper is to find a matching constructor based on some given arguments and some optional
 * prefix arguments. See {@link #resolve()} for details.
 * <p>
 * Usage example:
 * <pre>
 *     class Foo {
 *         public Foo(String a) {...}                     // 1
 *         public Foo(String a, Integer b) {...}          // 2
 *         public Foo(String a, Integer b, Long c) {...}  // 3
 *     }
 *
 *
 *     // invoke constructor 1:
 *     Foo foo = new ConstructionHelper(Foo.class).withArguments("bar").resolve().invoke();
 *
 *     // invoke constructor 2:
 *     Foo foo = new ConstructionHelper(Foo.class).withArguments("bar", 5).resolve().invoke();
 *
 *     // invoke constructor 3:
 *     Foo foo = new ConstructionHelper(Foo.class).withOptionalPrefixArguments("bar").withArguments(5, 47L).resolve().invoke();
 *
 *     // invoke constructor 2:
 *     Foo foo = new ConstructionHelper(Foo.class).withOptionalPrefixArguments(47L).withArguments("bar", 5).resolve().invoke();
 *
 *     // invoke constructor 3:
 *     Foo foo = new ConstructionHelper(Foo.class).withOptionalPrefixArguments("bar", 5).withArguments(47L).resolve().invoke();
 * </pre>
 * </p>
 */
public class ConstructionHelper<T> {

    private final Class<T> beanType;
    private final Object[] args;
    private final Object[] optionalPrefixArgs;
    // TODO: Can't compile. Shouldn't it be `Constructor<T>`? 
    private Constructor resolvedConstructor;
    private Object[] resolvedArguments;

    public ConstructionHelper(Class<T> beanType) {
        this(beanType, ArrayUtils.EMPTY_OBJECT_ARRAY, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    protected ConstructionHelper(Class<T> beanType, Object[] args, Object[] optionalPrefixArgs) {
        this.beanType = beanType;
        this.args = args;
        this.optionalPrefixArgs = optionalPrefixArgs;
    }

    public ConstructionHelper<T> withArguments(Object... args) {
        return new ConstructionHelper<T>(beanType, args, optionalPrefixArgs);
    }

    public ConstructionHelper<T> withOptionalPrefixArguments(Object... optionalPrefixArgs) {
        return new ConstructionHelper<T>(beanType, args, optionalPrefixArgs);
    }

    /**
     * Try to find a matching constructor for the given bean type.
     * The matching is done in two steps:
     * <ol>
     *     <li>Look for a constructor with argument types matching all the prefix arguments and the normal arguments</li>
     *     <li>Look for a constructor with argument types matching only the normal arguments</li>
     * </ol>
     * @return {@link ConstructionHelper} instance with resolved constructor matching result
     * @throws java.lang.IllegalArgumentException if no matching constructor could be found
     */
    @SuppressWarnings("unchecked")
    public ConstructionHelper<T> resolve() {
        Class[] parameterTypes;
        Constructor<T> constructor;

        if (optionalPrefixArgs.length > 0) {
            // 1. try with prefix arguments
            Object[] prefixedArgs = new Object[optionalPrefixArgs.length + args.length];
            System.arraycopy(optionalPrefixArgs, 0, prefixedArgs, 0, optionalPrefixArgs.length);
            System.arraycopy(args, 0, prefixedArgs, optionalPrefixArgs.length, args.length);
            parameterTypes = getParameterTypes(prefixedArgs);
            constructor = ConstructorUtils.getMatchingAccessibleConstructor(beanType, parameterTypes);
            if (constructor != null) {
                resolvedConstructor = constructor;
                resolvedArguments = prefixedArgs;
                return this;
            }
        }


        // 2. try without prefix arguments
        parameterTypes = getParameterTypes(args);
        constructor = ConstructorUtils.getMatchingAccessibleConstructor(beanType, parameterTypes);
        if (constructor != null) {
            resolvedConstructor = constructor;
            resolvedArguments = args;
            return this;
        }

        throw new IllegalArgumentException("Could not create new instance of type " + beanType.getName() + ". No matching constructor found, expected argument types: " + typesAsString());
    }

    public Object[] getResolvedArguments() {
        if (resolvedArguments == null) {
            throw new IllegalStateException("Not yet resolved - you must call resolve() first!");
        }
        return resolvedArguments;
    }

    public Class[] getResolvedArgumentTypes() {
        if (resolvedConstructor == null) {
            throw new IllegalStateException("Not yet resolved - you must call resolve() first!");
        }
        return resolvedConstructor.getParameterTypes();
    }

    @SuppressWarnings("unchecked")
    public T invoke() {
        if (resolvedConstructor == null || resolvedArguments == null) {
            throw new IllegalStateException("Not yet resolved - you must call resolve() first!");
        }
        return invoke(resolvedConstructor, resolvedArguments);
    }

    private T invoke(Constructor<T> constructor, Object[] args) {
        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new IllegalStateException("Could not create new instance of type " + beanType.getName() + ". Error invoking constructor: " + e.getMessage(), e);
        }
    }

    private Class[] getParameterTypes(Object[] args) {
        Class parameterTypes[] = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            parameterTypes[i] = arg != null ? arg.getClass() : Object.class;
        }
        return parameterTypes;
    }

    private String typesAsString() {
        StringBuilder sb = new StringBuilder();
        for (Class type : getParameterTypes(optionalPrefixArgs)) {
            if (sb.length() > 0) {
                sb = sb.append(',');
            }
            sb = sb.append(type.getSimpleName()).append(" (optional)");
        }
        for (Class type : getParameterTypes(args)) {
            if (sb.length() > 0) {
                sb = sb.append(',');
            }
            sb = sb.append(type.getSimpleName());
        }
        return sb.toString();
    }

}
