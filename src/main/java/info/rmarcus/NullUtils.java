// < begin copyright > 
// Copyright Ryan Marcus 2017
// 
// This file is part of birkhoffvonneumann.
// 
// birkhoffvonneumann is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// birkhoffvonneumann is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with birkhoffvonneumann.  If not, see <http://www.gnu.org/licenses/>.
// 
// < end copyright > 
 
package info.rmarcus;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class NullUtils {
	public static <T> T orThrow(@Nullable T t, Supplier<@NonNull RuntimeException> re) {
		if (t == null) {
			@Nullable RuntimeException tt = re.get();
			throw tt;
		}
		
		return t;
	}
	
	public static <T> T[] orThrow(T @Nullable [] t, Supplier<@NonNull RuntimeException> re) {
		if (t == null) {
			@Nullable RuntimeException tt = re.get();
			throw tt;
		}
		
		return t;
	}

	
	public static <T> @Nullable T deoptional(@Nullable Optional<@NonNull T> min) {
		if (min != null && min.isPresent())
			return min.get();
		
		return null;
	}
	
	public static <T> SafeOpt<T> wrap(@Nullable Optional<T> opt) {
		if (opt == null)
			return new SafeOpt<T>((@Nullable T) null);
		
		try {
			@Nullable final T tmp = opt.get();
			if (tmp != null) {
				return new SafeOpt<T>(tmp);
			}
		} catch (NoSuchElementException e) {
			
		}
		return new SafeOpt<T>((@Nullable T)null);
		
	}
	
	@FunctionalInterface
	public interface CheckedBiFunction<U, V, T> {
	   T apply(U arg1, V arg2) throws Exception;
	}
	
	@FunctionalInterface
	public interface CheckedConsumer<U> {
	   void apply(U arg1) throws Exception;
	}
	
	public static <U, V, T> SafeOpt<T> wrapCall(CheckedBiFunction<U, V, T> f, U arg1, V arg2) {
		try {
			T res = f.apply(arg1, arg2);
			return new SafeOpt<T>(res);
		} catch (Exception e) {
			return new SafeOpt<T>(e);
		}
	}
	
	public static <U> SafeOpt<Boolean> wrapCall(CheckedConsumer<U> f, U arg1) {
		try {
			f.apply(arg1);
			return new SafeOpt<Boolean>(Boolean.valueOf(true));
		} catch (Exception e) {
			return new SafeOpt<Boolean>(e);
		}
	}
	
	
	public static class SafeOpt<T> {
		@Nullable private final T val;
		@Nullable private final Exception e;
		
		public SafeOpt(@Nullable T t) {
			this.val = t;
			e = null;
		}
		
		public SafeOpt(@Nullable Exception e) {
			val = null;
			this.e = e;
		}
		
		public @NonNull T get() {
			@Nullable final T v = val;
			if (v == null) {
				throw new RuntimeException("Getting a null value!");
			}
			
			return v;
		}
				
		public Exception getException() {
			@Nullable final Exception v = e;
			if (v == null) {
				throw new RuntimeException("SafeOpt did not have an exception!");
			}
			
			return v;
		}
		
		public boolean hasValue() {
			return val != null;
		}
		
		public <K> SafeOpt<K> map(@Nullable Function<T, @Nullable K> f) {
			@Nullable final T v = val;
			if (v == null || f == null)
				return new SafeOpt<K>(e);
			
			return new SafeOpt<K>(f.apply(v));
		}
		
		public SafeOpt<Double> apply(@Nullable ToDoubleFunction<T> f) {
			@Nullable final T v = val;
			if (v == null || f == null)
				return new SafeOpt<Double>(e);
			
			return new SafeOpt<Double>(Double.valueOf(f.applyAsDouble(v)));
		}
		
		public void throwIfExceptionPresent() {
			final Exception exp = e;
			if (exp != null) {
				throw new RuntimeException(exp.getMessage());
			}
		}
	}
	
	public static class Pair<T, V> {
		public T a;
		public V b;
		
		public Pair(T a, V b) {
			this.a = a;
			this.b = b;
		}
	}



}
