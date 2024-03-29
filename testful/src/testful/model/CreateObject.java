/*
 * TestFul - http://code.google.com/p/testful/
 * Copyright (C) 2010  Matteo Miraz
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package testful.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import testful.model.MethodInformation.ParameterInformation;
import ec.util.MersenneTwisterFast;

public class CreateObject extends Operation {

	private static final long serialVersionUID = -7495725315460017277L;

	private final Reference ref;
	private final Constructorz constructor;
	private final Reference[] params;

	public CreateObject(Reference ref, Constructorz constructor, Reference[] params) {
		super();
		this.ref = ref;
		this.constructor = constructor;
		this.params = params;
	}

	@Override
	public Operation adapt(TestCluster cluster, ReferenceFactory refFactory) {
		final CreateObject ret = new CreateObject(refFactory.adapt(ref), cluster.adapt(constructor), refFactory.adapt(params));

		Iterator<OperationInformation> it = getInfos();
		while(it.hasNext()) {
			OperationInformation info = it.next();
			ret.addInfo(info.clone());
		}

		return ret;
	}

	public static CreateObject generate(AtomicLong idGenerator, Clazz c, TestCluster cluster, ReferenceFactory refFactory, MersenneTwisterFast random) {
		if(c.getConstructors().length == 0) return null;

		Constructorz constructor = c.getConstructors()[random.nextInt(c.getConstructors().length)];
		Reference[] params = new Reference[constructor.getParameterTypes().length];
		for(int i = 0; i < params.length; i++) {
			params[i] = Operation.generateRef(constructor.getParameterTypes()[i], cluster, refFactory, random);
			if(params[i] == null) return null;
		}

		final Reference ref;
		final Clazz[] assignableTo = c.getAssignableTo();
		if(assignableTo.length > 0) ref = refFactory.getReference(assignableTo[random.nextInt(assignableTo.length)], random);
		else ref = refFactory.getReference(c, random);

		return new CreateObject(ref, constructor, params);
	}

	public Reference getTarget() {
		return ref;
	}

	public Constructorz getConstructor() {
		return constructor;
	}

	public Reference[] getParams() {
		return params;
	}

	@Override
	public String toString() {
		StringBuilder pars = null;
		for(Reference p : params) {
			if(pars == null) pars = new StringBuilder();
			else pars.append(", ");
			pars.append(p);
		}

		if(ref == null)
			return "new " + constructor.getClazz().getClassName() + "(" + (pars != null ? pars.toString() : "") + ")";

		if(ref.getClazz() instanceof PrimitiveClazz)
			return ref + " = " + ((PrimitiveClazz) ref.getClazz()).getCast() + " new " + constructor.getClazz().getClassName() + "(" + (pars != null ? pars.toString() : "") + ")";
		else
			return ref + " = new " + constructor.getClazz().getClassName() + "(" + (pars != null ? pars.toString() : "") + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
		result = prime * result + constructor.hashCode();
		result = prime * result + Arrays.hashCode(params);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof CreateObject)) return false;

		CreateObject other = (CreateObject) obj;
		return ref == null ? other.ref == null : ref.equals(other.ref)
				&& constructor.equals(other.constructor) && Arrays.equals(params, other.params);
	}

	@Override
	protected Set<Reference> calculateDefs() {
		Set<Reference> defs = new HashSet<Reference>();

		if(getTarget() != null)
			defs.add(getTarget());

		ParameterInformation[] pInfo = getConstructor().getMethodInformation().getParameters();
		for(int i = 0; i < pInfo.length; i++)
			if(pInfo[i].isCaptured() || pInfo[i].isCapturedByReturn() || pInfo[i].isMutated())
				defs.add(getParams()[i]);

		return defs;
	}

	@Override
	protected Set<Reference> calculateUses() {
		Set<Reference> uses = new HashSet<Reference>();

		for(Reference u : getParams())
			uses.add(u);

		return uses;
	}

	@Override
	public Operation clone() {
		return new CreateObject(ref, constructor, params);
	}
}
