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
package de.androidbytes.adbconnect.presentation.services;

import android.app.Service;


public abstract class BaseService extends Service {

    public abstract void checkCurrentWirelessAdbState();
    public abstract void toggleWirelessAdbState(boolean setActive);
    public abstract void evaluateCurrentIpAddress();
    public abstract void checkAdbConnectRequirements();

}
