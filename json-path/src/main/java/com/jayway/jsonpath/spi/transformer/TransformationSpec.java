package com.jayway.jsonpath.spi.transformer;

import com.sun.xml.internal.ws.developer.ValidationErrorHandler;

import java.util.List;

/**
 * A Tagging interface used to designate the Transformation Specification.
 * The details of the Spec are a Property of the Provider.
 */

public interface TransformationSpec {

    /**
     * @return the Transformation Spec Object. The exact structure of the
     * spec is a choice of the Provider.
     */
    Object get();

    /**
     *
     * @return a list of Validation Errors in the TransformationSpec
     */
    List<ValidationError> validate();

}
