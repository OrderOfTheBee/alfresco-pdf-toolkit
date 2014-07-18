<%--
 * Copyright 2008-2012 Alfresco Software Limited.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * This file is part of an unsupported extension to Alfresco.
 --%>
<tr>
	<td><h:outputText value="#{customMsg.pdfstamp_position}" />:</td>
	<td><h:selectOneMenu id="StampPosition"
			value="#{WizardManager.bean.actionProperties.StampPosition}">
			<f:selectItems
				value="#{WizardManager.bean.actionProperties.PositionOptions}" />
		</h:selectOneMenu></td>
</tr>
<tr>
	<td class="paddingRow">OR:</td>
</tr>
<tr>
	<td><h:outputText value="#{customMsg.pdfstamp_location_x}" />:</td>
	<td><h:inputText id="location_x" size="4"
			value="#{WizardManager.bean.actionProperties.LocationX}" /></td>
</tr>
<tr>
	<td class="paddingRow"></td>
</tr>
<tr>
	<td><h:outputText value="#{customMsg.pdfstamp_location_y}" />:</td>
	<td><h:inputText id="location_y" size="4"
			value="#{WizardManager.bean.actionProperties.LocationY}" /></td>
</tr>

