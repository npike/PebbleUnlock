Pebble Unlock
====================
Pebble Unlock is a simple Android app that acts as a Device Administrator using the [device policy manager API](https://developer.android.com/reference/android/app/admin/DevicePolicyManager.html).

It depends on the [Pebble App](https://play.google.com/store/apps/details?id=com.getpebble.android) being installed to watch for broadcast intents for connection events.


Testing
=======

To simulate pebble connected:

am broadcast -a com.getpebble.action.PEBBLE_CONNECTED -e address foo

To simulate pebble disconnected:

am broadcast -a com.getpebble.action.PEBBLE_DISCONNECTED   -e address foo



License
=======

    Copyright 2013 Nicholas Pike

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.