/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vaadin.s4v.instrument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.bytecode.ClassFile;

import com.vaadin.s4v.util.UiUtil;

/**
 * @author Serkan ÖZAL
 * 
 * Contact Informations:
 * 		GitHub   : https://github.com/serkan-ozal
 * 		LinkedIn : www.linkedin.com/in/serkanozal
 */
public class VaadinComponentClassTransformer implements ClassFileTransformer {
	
	protected ClassPool cp = ClassPool.getDefault();
    
	protected CtClass buildClass(byte[] bytes) throws IOException, RuntimeException {
        return cp.makeClass(new ByteArrayInputStream(bytes));
    }
	
	protected boolean isAbstractVaadinComponent(CtClass clazz) {
		return clazz.getName().equals("com.vaadin.ui.AbstractComponent");
	}
	
	protected boolean isVaadinComponentButNotExtendsFromAbstractVaadinComponent(CtClass clazz) {
		try {
			CtClass[] interfacesDirectly = clazz.getInterfaces();
			if (interfacesDirectly != null) {
				for (CtClass interfaceDirectly : interfacesDirectly) {
					if (interfaceDirectly.getName().equals("com.vaadin.ui.Component")) {
						return true;
					}
				}
			}
			
	        String cname = clazz.getName();
	
	        ClassFile file = clazz.getClassFile2();
	        String supername = file.getSuperclass();
	        if (supername != null && supername.equals(cname))
	            return true;
	
	        String[] ifs = file.getInterfaces();
	        int num = ifs.length;
	        for (int i = 0; i < num; ++i) {
	            if (ifs[i].equals(cname)) {
	                return true;
	            }
	        }
	        
	        if (supername != null && cp.get(supername).subtypeOf(clazz)) {
	            return true;
	        }
	        
	        for (int i = 0; i < num; ++i) { 
	            if (cp.get(ifs[i]).subtypeOf(clazz)) {
	                return true;
	            }
	        }    
		}
		catch (Throwable t) {
			
		}
		
		return false;
	}
	
	protected boolean mustBeInstrumented(CtClass clazz) {
		if (clazz.isInterface()) {
			return false;
		}
		return 
			isAbstractVaadinComponent(clazz) || 
			isVaadinComponentButNotExtendsFromAbstractVaadinComponent(clazz);
	}
	
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, 
			ProtectionDomain domain, byte[] bytes) throws IllegalClassFormatException {
        try {
        	CtClass ct = buildClass(bytes);
        	if (mustBeInstrumented(ct)) {
	            System.out.println("[INFO] : " + "Class " + className + " is being instrumented ...");
	            
	            cp.importPackage(UiUtil.class.getPackage().getName());
	            cp.appendClassPath(new ClassClassPath(UiUtil.class));
	            
	            // Ensure that there will be at least one class initializer (or constructor)
	            ct.makeClassInitializer();
	            
	            CtConstructor[] constructors = ct.getConstructors();
	            
	            for (CtConstructor c : constructors) {
	            	c.insertAfter("UiUtil.injectSpringBeans(this);");
	            }
	            
	            return ct.toBytecode();
        	}
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        
        return bytes;
    }
	
}
