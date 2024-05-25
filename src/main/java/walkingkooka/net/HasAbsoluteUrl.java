/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.net;

/**
 * A resource or entity which includes a {@link AbsoluteUrl} property, which is probably used to uniquely identify.
 */
public interface HasAbsoluteUrl {

    /**
     * A {@link AbsoluteUrl} which should uniquely identify this resource. It does not need to link to an actual webpage or similar web resource.
     */
    AbsoluteUrl url();
}
