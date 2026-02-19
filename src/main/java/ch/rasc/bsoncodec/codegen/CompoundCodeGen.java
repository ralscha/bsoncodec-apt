/*
 * Copyright the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.rasc.bsoncodec.codegen;

import javax.lang.model.type.TypeMirror;

public abstract class CompoundCodeGen implements CodeGen {

	private final TypeMirror type;
	private CodeGen childCodeGen;
	private final boolean hasParent;

	public CompoundCodeGen(CompoundCodeGen parent, TypeMirror type) {
		if (parent != null) {
			parent.childCodeGen = this;
			this.hasParent = true;
		}
		else {
			this.hasParent = false;
		}
		this.type = type;
	}

	@Override
	public TypeMirror getType() {
		return this.type;
	}

	protected CodeGen getChildCodeGen() {
		return this.childCodeGen;
	}

	protected boolean hasParent() {
		return this.hasParent;
	}

}
