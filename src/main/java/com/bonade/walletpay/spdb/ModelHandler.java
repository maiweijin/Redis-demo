package com.bonade.walletpay.spdb;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ModelHandler implements MethodInterceptor {
	private Map<Object, InvokeMethod> methodMap = new HashMap<>();

	private Random random = new Random();

	public Method getMethod(Object key) {
		return methodMap.get(key).getMethod();
	}

	public Object[] getArgs(Object key) {
		return methodMap.get(key).getArgs();
	}

	class InvokeMethod {
		private Method method;
		private Object[] args;

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}

		public Object[] getArgs() {
			return args;
		}

		public void setArgs(Object[] args) {
			this.args = args;
		}

		public InvokeMethod(Method method, Object[] args) {
			this.method = method;
			this.args = args;
		}
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		Class<?> returnType = method.getReturnType();
		Object key = null;
		if (String.class.isAssignableFrom(returnType)) {
			key = UUID.randomUUID().toString();
		} else if (Number.class.isAssignableFrom(returnType)) {
			do {
				key = random.nextInt();
			} while (methodMap.containsKey(key));
		}else {
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(returnType);
			enhancer.setCallback(this);
			enhancer.setClassLoader(returnType.getClassLoader());
			key = enhancer.create();
		}
		methodMap.put(key, new InvokeMethod(method, args));
		return key;
	}
}
