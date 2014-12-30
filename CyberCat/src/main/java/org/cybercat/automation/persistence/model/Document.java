/**Copyright 2013 The Cybercat project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cybercat.automation.persistence.model;

public class Document {

    String internalId;

    String country;

    String language;

    String targetProduct;
    
    String docType;
    
    String contentType; 
    
    String heading;

   
    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getContentType() {
        return contentType;
    }   

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTargetProduct() {
        return targetProduct;
    }

    public void setTargetProduct(String targetProduct) {
        this.targetProduct = targetProduct;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((docType == null) ? 0 : docType.hashCode());
        result = prime * result + ((internalId == null) ? 0 : internalId.hashCode());
        result = prime * result + ((language == null) ? 0 : language.hashCode());
        result = prime * result + ((targetProduct == null) ? 0 : targetProduct.hashCode());
        result = prime * result + ((heading == null) ? 0 : heading.hashCode());
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Document other = (Document) obj;
        if (contentType == null) {
            if (other.contentType != null)
                return false;
        } else if (!contentType.equals(other.contentType))
            return false;
        if (country == null) {
            if (other.country != null)
                return false;
        } else if (!country.equals(other.country))
            return false;
        if (docType == null) {
            if (other.docType != null)
                return false;
        } else if (!docType.equals(other.docType))
            return false;
        if (internalId == null) {
            if (other.internalId != null)
                return false;
        } else if (!internalId.equals(other.internalId))
            return false;
        if (language == null) {
            if (other.language != null)
                return false;
        } else if (!language.equals(other.language))
            return false;
        if (targetProduct == null) {
            if (other.targetProduct != null)
                return false;
        } else if (!targetProduct.equals(other.targetProduct))
            return false;
        if (heading == null) {
            if (other.heading != null)
                return false;
        } else if (!heading.equals(other.heading))
            return false;
        return true;
    }



    @Override
    public String toString() {
        return "Document [internalId=" + internalId + ", country=" + country + ", language=" + language + ", targetProduct="
                + targetProduct + ", docType=" + docType + ", contentType=" + contentType + ", title=" + heading + "]";
    }
    
   
    
    

}
