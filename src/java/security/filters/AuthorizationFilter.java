/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security.filters;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import model.User;
import security.Role;
import security.Secured;
import security.SecurityManager;

/**
 *
 * @author Dhaval
 */
@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter
{

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException
    {

	// Get the HTTP Authorization header from the request
	String authorizationHeader
		= requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

	// Extract the token from the HTTP Authorization header
	String token = authorizationHeader.substring("Bearer".length()).trim();

	// Get the resource class which matches with the requested URL
	// Extract the roles declared by it
	Class<?> resourceClass = resourceInfo.getResourceClass();
	Set<Role> classRoles = extractRoles(resourceClass);

	// Get the resource method which matches with the requested URL
	// Extract the roles declared by it
	Method resourceMethod = resourceInfo.getResourceMethod();
	Set<Role> methodRoles = extractRoles(resourceMethod);

	try
	{

	    // Check if the user is allowed to execute the method
	    // The method annotations override the class annotations
	    if (methodRoles.isEmpty())
	    {
		checkPermissions(classRoles, token);
	    }
	    else
	    {
		checkPermissions(methodRoles, token);
	    }

	}
	catch (Exception e)
	{
	    requestContext.abortWith(
		    Response.status(Response.Status.FORBIDDEN).build());
	}
    }

    // Extract the roles from the annotated element
    private Set<Role> extractRoles(AnnotatedElement annotatedElement)
    {
	if (annotatedElement == null)
	{
	    return new HashSet<>();
	}
	else
	{
	    Secured secured = annotatedElement.getAnnotation(Secured.class);

	    System.err.println(Arrays.toString(annotatedElement.getDeclaredAnnotations()));

	    if (secured == null)
	    {
		return new HashSet<>();
	    }

	    Set<Role> roles = new HashSet<>();
	    Role[] allowedRoles = secured.roles();
	    roles.addAll(Arrays.asList(allowedRoles));
	    return roles;
	}
    }

    private void checkPermissions(Set<Role> allowedRoles, String token) throws Exception
    {
	User user = SecurityManager.getUser(token);

	for (Role allowedRole : allowedRoles)
	{
	    if (user.getRoles().contains(allowedRole))
	    {
		return;
	    }
	}

	throw new RuntimeException("Unauthorized");
    }
}
