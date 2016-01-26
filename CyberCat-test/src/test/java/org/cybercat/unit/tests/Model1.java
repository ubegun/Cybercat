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

package org.cybercat.unit.tests;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.cybercat.automation.persistence.model.Entity;
import org.cybercat.automation.utils.CommonUtils;

/**
 * @author Ubegun
 * 
 */
@XmlRootElement(name = "Model1")
public class Model1 implements Entity {

    public static enum TestEnum {
        e1, e2
    };

    private long id = CommonUtils.genGuid();
    private String sField;
    private TestEnum emumField;
    private List<Model2> model2;

    public String getsField() {
        return sField;
    }

    public void setsField(String sField) {
        this.sField = sField;
    }

    public TestEnum getEmumField() {
        return emumField;
    }

    public void setEmumField(TestEnum emumField) {
        this.emumField = emumField;
    }

    public List<Model2> getModel2() {
        return model2;
    }

    public void setModel2(List<Model2> model2) {
        this.model2 = model2;
    }

    /* (non-Javadoc)
     * @see org.cybercat.automation.persistence.model.Entity#getId()
     */
    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
