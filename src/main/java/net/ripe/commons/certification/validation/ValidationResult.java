/**
 * The BSD License
 *
 * Copyright (c) 2010, 2011 RIPE NCC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   - Neither the name of the RIPE NCC nor the names of its contributors may be
 *     used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.ripe.commons.certification.validation;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ValidationResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, Set<ValidationCheck>> validationCheck = new LinkedHashMap<String, Set<ValidationCheck>>();

	private String currentLocation;
	private Set<ValidationCheck> currentCheckList;

	public void push(URI location) {
	    setLocation(location.toString());
	}

	public void setLocation(String location) {
		currentLocation = location;
		currentCheckList = validationCheck.get(currentLocation);

		if (currentCheckList == null) {
			currentCheckList = new LinkedHashSet<ValidationCheck>();
			validationCheck.put(currentLocation, currentCheckList);
		}
	}

	public boolean isTrue(boolean condition, String key, Object... param) {
	    Validate.notNull(key, "key is required");
		currentCheckList.add(new ValidationCheck(condition, key, param));
		return condition;
	}

	public boolean isFalse(boolean condition, String key, Object... param) {
	    return isTrue(!condition, key, param);
	}

	public boolean notNull(Object object, String key, Object... param) {
	    return isTrue(object != null, key, param);
	}

	public boolean hasFailures() {
		for (Set<ValidationCheck> list : validationCheck.values()) {
			for (ValidationCheck vc : list) {
				if (!vc.isOk()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasFailureForLocation(String location) {
		if (validationCheck.containsKey(location)) {
			for (ValidationCheck vc : validationCheck.get(location)) {
				if (!vc.isOk()) {
					return true;
				}
			}
		}
		return false;
	}

    public boolean hasFailureForCurrentLocation() {
        return hasFailureForLocation(currentLocation);
    }

    public boolean hasFailuresForLocationAndKey(String location, String key) {
        ValidationCheck check = getResult(location, key);
        return check != null && !check.isOk();
    }

	public List<ValidationCheck> getFailures(String location) {
	    Validate.isTrue(validationCheck.containsKey(location));
		List<ValidationCheck> failedList = new ArrayList<ValidationCheck>();
		for (ValidationCheck vc : validationCheck.get(location)) {
			if (!vc.isOk()) {
				failedList.add(vc);
			}
		}
		return failedList;
	}

	public ValidationCheck getResult(String location, String key) {
	    Validate.isTrue(validationCheck.containsKey(location));
	    Validate.notNull(key);
	    for (ValidationCheck result: validationCheck.get(location)) {
	        if (key.equals(result.getKey())) {
	            return result;
	        }
	    }
	    return null;
	}

    public ValidationCheck getResult(URI uri, String key) {
        return getResult(uri.toString(), key);
    }

	public Set<ValidationCheck> getResults(String location) {
        Validate.isTrue(validationCheck.containsKey(location));
		return validationCheck.get(location);
	}

	public Set<String> getValidatedLocations() {
		return validationCheck.keySet();
	}

	public Iterator<ValidationCheck> iterator(String location) {
        Validate.isTrue(validationCheck.containsKey(location));
		return validationCheck.get(location).iterator();
	}

	@Override
	public String toString() {
	    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public Set<ValidationCheck> getFailuresForCurrentLocation() {
		return new LinkedHashSet<ValidationCheck>(getFailures(currentLocation));
	}

    public Set<ValidationCheck> getResultsForCurrentLocation() {
        return getResults(currentLocation);
    }

    public String getCurrentLocation() {
        return currentLocation;
    }
}
