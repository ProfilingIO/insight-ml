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
package com.insightml.utils.io.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.objenesis.strategy.StdInstantiatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.KryoSerializableSerializer;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.insightml.utils.Check;
import com.insightml.utils.io.serialization.Serializers.IListSerializer;
import com.insightml.utils.types.collections.IList;
import com.insightml.utils.types.collections.ListFacade;

public final class Serialization implements ISerializer {

	private static final Serialization instance = new Serialization();
	private static final Logger logger = LoggerFactory.getLogger(Serialization.class);

	private Serialization() {
	}

	@Nonnull
	public static Serialization get() {
		return instance;
	}

	private static Kryo kryo() {
		final Kryo kryo = new Kryo();
		kryo.setRegistrationRequired(false);
		kryo.register(IList.class, new IListSerializer());
		kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
		kryo.addDefaultSerializer(ListFacade.class, KryoSerializableSerializer.class);
		return kryo;
	}

	@Override
	public <T> T serialize(final T obj, final String dir) {
		new File(dir).mkdirs();
		return serialize(new File(dir + obj.getClass().getSimpleName()), obj);
	}

	@Override
	@Nonnull
	public <T> T serialize(final File file, final T obj) {
		Check.notNull(obj);
		try (FileOutputStream stream = new FileOutputStream(file)) {
			serialize(stream, obj);
			return obj;
		} catch (final IOException e) {
			file.delete();
			throw new IllegalStateException(e);
		}
	}

	public static void serialize(final OutputStream outputStream, final Object obj) {
		try (Output out = new Output(outputStream)) {
			kryo().writeObject(out, obj);
		}
	}

	@Override
	@Nonnull
	public <T> T unserialize(final File file, final Class<T> clazz) throws IOException {
		if (false) {
			logger.info("Unserializing " + file);
		}
		try (final FileInputStream stream = new FileInputStream(file)) {
			return unserialize(stream, clazz);
		}
	}

	public static <T> T unserialize(final InputStream inputStream, final Class<T> clazz) throws IOException {
		try (Input in = new Input(inputStream)) {
			return kryo().readObject(in, Check.notNull(clazz));
		} catch (final KryoException e) {
			throw new IOException(e);
		}
	}

	@Override
	public <T> T loadOrCreate(final T object, final String dir) {
		final String className = object.getClass().getSimpleName();
		final File file = new File(dir + className);
		if (!file.exists()) {
			logger.info("Creating " + className + " instance.");
			return object;
		}
		try {
			return (T) unserialize(file, object.getClass());
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public <T extends Serializable> T loadOrCreate(final File file, final Supplier<T> creator, final Class<T> clazz) {
		file.getParentFile().mkdirs();
		if (file.exists()) {
			try {
				return unserialize(file, clazz);
			} catch (final Throwable e) {
				logger.error("{}", e, e);
			}
		}
		return serialize(file, creator.get());
	}

	public static <T> T deepCopy(final T oldObj) {
		try (final ByteArrayOutputStream bos = new ByteArrayOutputStream();
				final ObjectOutputStream oos = new ObjectOutputStream(bos);) {
			oos.writeObject(oldObj);
			oos.flush();
			final ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
			final ObjectInputStream ois = new ObjectInputStream(bin);
			return (T) ois.readObject();
		} catch (final IOException | ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
}
