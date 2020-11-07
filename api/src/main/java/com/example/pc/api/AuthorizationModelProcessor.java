package com.example.pc.api;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.security.authorization.method.PreAuthorizeAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.util.SimpleMethodInvocation;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AuthorizationModelProcessor implements
		RepresentationModelProcessor<EntityModel<PowerConverters>> {

	private final PreAuthorizeAuthorizationManager authorizationManager = new PreAuthorizeAuthorizationManager();

	@Override
	public EntityModel<PowerConverters> process(EntityModel<PowerConverters> model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PowerConvertersController controller = methodOn(PowerConvertersController.class);
		for (Method method : controller.getClass().getDeclaredMethods()) {
			MethodInvocation invocation = new SimpleMethodInvocation(controller, method, authentication);
			try {
				if (method.canAccess(controller)) {
					this.authorizationManager.verify(() -> authentication, invocation);
					model.add(linkTo(method, authentication).withRel(method.getName()));
				}
			} catch (Throwable ex) {
				// no problem, don't add link
			}
		}
		return model;
	}
}

