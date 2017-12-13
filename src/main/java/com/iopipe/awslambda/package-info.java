/**
 * This package contains the wrappers for
 * {@link com.amazonaws.services.lambda.runtime.RequestHandler}
 * and {@link com.amazonaws.services.lambda.runtime.RequestStreamHandler}.
 *
 * There are two types of wrappers for diferent use cases: extending and
 * calling.
 *
 * Extending wrappers are used when it is simple to modify an existing class
 * to provide lambda functionality (it implements interfaces and only extends
 * Object).
 *
 * Calling wrappers are used when a pre-existing class needs to be wrapped
 * however it extends another class which cannot or is not easily modified to
 * extend the extending handlers.
 *
 * @since 2017/12/12
 */

package com.iopipe.awslambda;

