/**
 * Copyright (C) 2016 Christoph Hennemann
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
package de.androidbytes.adbconnect.domain.interactor.result;


public enum AdbConnectRequirementsStateEnum {

    ADB_CONNECT_REQUIREMENTS_AVAILABLE("ADB_CONNECT_REQUIREMENTS_AVAILABLE"),
    ADB_CONNECT_REQUIREMENT_WIFI_NOT_ENABLED("ADB_CONNECT_REQUIREMENT_WIFI_NOT_ENABLED"),
    ADB_CONNECT_REQUIREMENT_WIFI_STATE_NOT_CONNECTED("ADB_CONNECT_REQUIREMENT_WIFI_STATE_NOT_CONNECTED"),
    ADB_CONNECT_REQUIREMENT_USB_DEBUGGING_DISABLED("ADB_CONNECT_REQUIREMENT_USB_DEBUGGING_DISABLED"),
    ADB_CONNECT_REQUIREMENT_DEVELOPER_OPTIONS_DISABLED("ADB_CONNECT_REQUIREMENT_DEVELOPER_OPTIONS_DISABLED");

    private final String fStateValue;

    AdbConnectRequirementsStateEnum(final String stateValue) {
        fStateValue = stateValue;
    }

    public String toString() {
        return fStateValue;
    }

}
