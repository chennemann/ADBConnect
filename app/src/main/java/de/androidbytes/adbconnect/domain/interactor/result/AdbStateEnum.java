package de.androidbytes.adbconnect.domain.interactor.result;


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
public enum AdbStateEnum {

    ADB_STATE_NO_ROOT_PERMISSIONS_GRANTED("ADB_STATE_NO_ROOT_PERMISSIONS_GRANTED"),
    ADB_STATE_WIRELESS_ADB_ACTIVE("ADB_STATE_WIRELESS_ADB_ACTIVE"),
    ADB_STATE_WIRELESS_ADB_TURNED_ON("ADB_STATE_WIRELESS_ADB_TURNED_ON"),
    ADB_STATE_WIRELESS_ADB_INACTIVE("ADB_STATE_WIRELESS_ADB_INACTIVE"),
    ADB_STATE_WIRELESS_ADB_TURNED_OFF("ADB_STATE_WIRELESS_ADB_TURNED_OFF");

    private final String fStateValue;

    AdbStateEnum(final String stateValue) {
        fStateValue = stateValue;
    }

    public String toString() {
        return fStateValue;
    }

}
