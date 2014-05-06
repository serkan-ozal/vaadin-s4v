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

package com.vaadin.s4v;

import java.lang.instrument.UnmodifiableClassException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.vaadin.s4v.instrument.VaadinComponentClassTransformer;
import com.vaadin.ui.AbstractComponent;

import tr.com.serkanozal.jillegal.agent.JillegalAgent;

/**
 * @author Serkan ÖZAL
 * 
 * Contact Informations:
 * 		GitHub   : https://github.com/serkan-ozal
 * 		LinkedIn : www.linkedin.com/in/serkanozal
 */
@Component
public class SpringIntegrator {

	static {
		JillegalAgent.init();
	}
	
	private static volatile boolean integrated = false;
	
	@PostConstruct
	protected void init() {
		integrateVaadinWithSpring();
	}
	
	public synchronized static void integrateVaadinWithSpring() {
		if (!integrated) {
			JillegalAgent.getInstrumentation().addTransformer(new VaadinComponentClassTransformer(), true);
			try {
				JillegalAgent.getInstrumentation().retransformClasses(AbstractComponent.class);
				
				integrated = true;
			} 
			catch (UnmodifiableClassException e) {
				e.printStackTrace();
			}
		}	
	}
	
}
