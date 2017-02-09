package com.java.lang;

import java.lang.String;
import java.lang.System;
import java.lang.Thread;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;

public class ThreadTest {
	
	@Test
	public void testContextClassLoader(){
		System.out.println(Thread.currentThread().getContextClassLoader());
	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		System.out.println(new URLClassLoader(new URL[0]).getParent());
		Thread.currentThread().setContextClassLoader(new URLClassLoader(new URL[0]));
		System.out.println(Thread.currentThread().getContextClassLoader().getParent());
	}
}
