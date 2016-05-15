/*
 * Copyright (C) 2016 Stefan Henß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.insightml.nlp;

import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractClass;

public abstract class AbstractToken extends AbstractClass implements IToken {
	private static final long serialVersionUID = -452709284497608375L;

	private final String original;
	private final boolean isStemmed;
	private String pos;
	private transient Integer hashCode;

	AbstractToken(final String original, final boolean isStemmed, final String pos) {
		Check.argument(!isStemmed || original != null || true);
		this.original = original;
		this.isStemmed = isStemmed;
		if (pos != null) {
			setPos(pos);
		}
	}

	@Override
	public final String getOriginal() {
		return original;
	}

	@Override
	public final boolean isStemmed() {
		return isStemmed;
	}

	@Override
	public final String getPos() {
		return pos;
	}

	@Override
	public final void setPos(final String tag) {
		if (getPos() != null) {
			throw new IllegalStateException(this + " is already tagged with " + getPos() + ". Trying to tag " + tag);
		}
		pos = Check.length(tag, 2, 5);
	}

	@Override
	public final int hashCode() {
		if (hashCode == null) {
			hashCode = toString().hashCode();
		}
		return hashCode;
	}

	@Override
	public final boolean equals(final Object obj) {
		Check.state(!(obj instanceof CharSequence), obj);
		return this == obj
				|| length() == ((IToken) obj).length() && hashCode() == obj.hashCode() && equals((IToken) obj);
	}

	abstract boolean equals(final IToken obj);

	@Override
	public final int compareTo(final IToken o) {
		return toString().compareTo(Check.notNull(o.toString()));
	}
}
