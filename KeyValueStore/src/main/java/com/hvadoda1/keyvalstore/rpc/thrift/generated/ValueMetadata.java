package com.hvadoda1.keyvalstore.rpc.thrift.generated;

import com.hvadoda1.keyvalstore.IValueMeta;

/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 * 
 * @generated
 */
@SuppressWarnings({ "cast", "rawtypes", "serial", "unchecked", "unused" })
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2020-11-26")
public class ValueMetadata implements org.apache.thrift.TBase<ValueMetadata, ValueMetadata._Fields>,
		java.io.Serializable, Cloneable, Comparable<ValueMetadata>, IValueMeta {
	private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
			"ValueMetadata");

	private static final org.apache.thrift.protocol.TField TIMESTAMP_FIELD_DESC = new org.apache.thrift.protocol.TField(
			"timestamp", org.apache.thrift.protocol.TType.I64, (short) 1);

	private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new ValueMetadataStandardSchemeFactory();
	private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new ValueMetadataTupleSchemeFactory();

	public long timestamp; // required

	/**
	 * The set of fields this struct contains, along with convenience methods for
	 * finding and manipulating them.
	 */
	public enum _Fields implements org.apache.thrift.TFieldIdEnum {
		TIMESTAMP((short) 1, "timestamp");

		private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

		static {
			for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
				byName.put(field.getFieldName(), field);
			}
		}

		/**
		 * Find the _Fields constant that matches fieldId, or null if its not found.
		 */
		@org.apache.thrift.annotation.Nullable
		public static _Fields findByThriftId(int fieldId) {
			switch (fieldId) {
			case 1: // TIMESTAMP
				return TIMESTAMP;
			default:
				return null;
			}
		}

		/**
		 * Find the _Fields constant that matches fieldId, throwing an exception if it
		 * is not found.
		 */
		public static _Fields findByThriftIdOrThrow(int fieldId) {
			_Fields fields = findByThriftId(fieldId);
			if (fields == null)
				throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
			return fields;
		}

		/**
		 * Find the _Fields constant that matches name, or null if its not found.
		 */
		@org.apache.thrift.annotation.Nullable
		public static _Fields findByName(java.lang.String name) {
			return byName.get(name);
		}

		private final short _thriftId;
		private final java.lang.String _fieldName;

		_Fields(short thriftId, java.lang.String fieldName) {
			_thriftId = thriftId;
			_fieldName = fieldName;
		}

		public short getThriftFieldId() {
			return _thriftId;
		}

		public java.lang.String getFieldName() {
			return _fieldName;
		}
	}

	// isset id assignments
	private static final int __TIMESTAMP_ISSET_ID = 0;
	private byte __isset_bitfield = 0;
	public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
	static {
		java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
				_Fields.class);
		tmpMap.put(_Fields.TIMESTAMP,
				new org.apache.thrift.meta_data.FieldMetaData("timestamp",
						org.apache.thrift.TFieldRequirementType.DEFAULT,
						new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
		metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
		org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ValueMetadata.class, metaDataMap);
	}

	public ValueMetadata() {
	}

	public ValueMetadata(long timestamp) {
		this();
		this.timestamp = timestamp;
		setTimestampIsSet(true);
	}

	/**
	 * Performs a deep copy on <i>other</i>.
	 */
	public ValueMetadata(ValueMetadata other) {
		__isset_bitfield = other.__isset_bitfield;
		this.timestamp = other.timestamp;
	}

	public ValueMetadata deepCopy() {
		return new ValueMetadata(this);
	}

	@Override
	public void clear() {
		setTimestampIsSet(false);
		this.timestamp = 0;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public ValueMetadata setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		setTimestampIsSet(true);
		return this;
	}

	public void unsetTimestamp() {
		__isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __TIMESTAMP_ISSET_ID);
	}

	/**
	 * Returns true if field timestamp is set (has been assigned a value) and false
	 * otherwise
	 */
	public boolean isSetTimestamp() {
		return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __TIMESTAMP_ISSET_ID);
	}

	public void setTimestampIsSet(boolean value) {
		__isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __TIMESTAMP_ISSET_ID, value);
	}

	public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
		switch (field) {
		case TIMESTAMP:
			if (value == null) {
				unsetTimestamp();
			} else {
				setTimestamp((java.lang.Long) value);
			}
			break;

		}
	}

	@org.apache.thrift.annotation.Nullable
	public java.lang.Object getFieldValue(_Fields field) {
		switch (field) {
		case TIMESTAMP:
			return getTimestamp();

		}
		throw new java.lang.IllegalStateException();
	}

	/**
	 * Returns true if field corresponding to fieldID is set (has been assigned a
	 * value) and false otherwise
	 */
	public boolean isSet(_Fields field) {
		if (field == null) {
			throw new java.lang.IllegalArgumentException();
		}

		switch (field) {
		case TIMESTAMP:
			return isSetTimestamp();
		}
		throw new java.lang.IllegalStateException();
	}

	@Override
	public boolean equals(java.lang.Object that) {
		if (that == null)
			return false;
		if (that instanceof ValueMetadata)
			return this.equals((ValueMetadata) that);
		return false;
	}

	public boolean equals(ValueMetadata that) {
		if (that == null)
			return false;
		if (this == that)
			return true;

		boolean this_present_timestamp = true;
		boolean that_present_timestamp = true;
		if (this_present_timestamp || that_present_timestamp) {
			if (!(this_present_timestamp && that_present_timestamp))
				return false;
			if (this.timestamp != that.timestamp)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hashCode = 1;

		hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(timestamp);

		return hashCode;
	}

	@Override
	public int compareTo(ValueMetadata other) {
		if (!getClass().equals(other.getClass())) {
			return getClass().getName().compareTo(other.getClass().getName());
		}

		int lastComparison = 0;

		lastComparison = java.lang.Boolean.valueOf(isSetTimestamp()).compareTo(other.isSetTimestamp());
		if (lastComparison != 0) {
			return lastComparison;
		}
		if (isSetTimestamp()) {
			lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.timestamp, other.timestamp);
			if (lastComparison != 0) {
				return lastComparison;
			}
		}
		return 0;
	}

	@org.apache.thrift.annotation.Nullable
	public _Fields fieldForId(int fieldId) {
		return _Fields.findByThriftId(fieldId);
	}

	public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
		scheme(iprot).read(iprot, this);
	}

	public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
		scheme(oprot).write(oprot, this);
	}

	@Override
	public java.lang.String toString() {
		java.lang.StringBuilder sb = new java.lang.StringBuilder("ValueMetadata(");
		boolean first = true;

		sb.append("timestamp:");
		sb.append(this.timestamp);
		first = false;
		sb.append(")");
		return sb.toString();
	}

	public void validate() throws org.apache.thrift.TException {
		// check for required fields
		// check for sub-struct validity
	}

	private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
		try {
			write(new org.apache.thrift.protocol.TCompactProtocol(
					new org.apache.thrift.transport.TIOStreamTransport(out)));
		} catch (org.apache.thrift.TException te) {
			throw new java.io.IOException(te);
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
		try {
			// it doesn't seem like you should have to do this, but java serialization is
			// wacky, and doesn't call the default constructor.
			__isset_bitfield = 0;
			read(new org.apache.thrift.protocol.TCompactProtocol(
					new org.apache.thrift.transport.TIOStreamTransport(in)));
		} catch (org.apache.thrift.TException te) {
			throw new java.io.IOException(te);
		}
	}

	private static class ValueMetadataStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
		public ValueMetadataStandardScheme getScheme() {
			return new ValueMetadataStandardScheme();
		}
	}

	private static class ValueMetadataStandardScheme extends org.apache.thrift.scheme.StandardScheme<ValueMetadata> {

		public void read(org.apache.thrift.protocol.TProtocol iprot, ValueMetadata struct)
				throws org.apache.thrift.TException {
			org.apache.thrift.protocol.TField schemeField;
			iprot.readStructBegin();
			while (true) {
				schemeField = iprot.readFieldBegin();
				if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
					break;
				}
				switch (schemeField.id) {
				case 1: // TIMESTAMP
					if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
						struct.timestamp = iprot.readI64();
						struct.setTimestampIsSet(true);
					} else {
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
					}
					break;
				default:
					org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
				}
				iprot.readFieldEnd();
			}
			iprot.readStructEnd();

			// check for required fields of primitive type, which can't be checked in the
			// validate method
			struct.validate();
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot, ValueMetadata struct)
				throws org.apache.thrift.TException {
			struct.validate();

			oprot.writeStructBegin(STRUCT_DESC);
			oprot.writeFieldBegin(TIMESTAMP_FIELD_DESC);
			oprot.writeI64(struct.timestamp);
			oprot.writeFieldEnd();
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}

	}

	private static class ValueMetadataTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
		public ValueMetadataTupleScheme getScheme() {
			return new ValueMetadataTupleScheme();
		}
	}

	private static class ValueMetadataTupleScheme extends org.apache.thrift.scheme.TupleScheme<ValueMetadata> {

		@Override
		public void write(org.apache.thrift.protocol.TProtocol prot, ValueMetadata struct)
				throws org.apache.thrift.TException {
			org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
			java.util.BitSet optionals = new java.util.BitSet();
			if (struct.isSetTimestamp()) {
				optionals.set(0);
			}
			oprot.writeBitSet(optionals, 1);
			if (struct.isSetTimestamp()) {
				oprot.writeI64(struct.timestamp);
			}
		}

		@Override
		public void read(org.apache.thrift.protocol.TProtocol prot, ValueMetadata struct)
				throws org.apache.thrift.TException {
			org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
			java.util.BitSet incoming = iprot.readBitSet(1);
			if (incoming.get(0)) {
				struct.timestamp = iprot.readI64();
				struct.setTimestampIsSet(true);
			}
		}
	}

	private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
		return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY
				: TUPLE_SCHEME_FACTORY).getScheme();
	}
}
