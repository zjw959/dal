// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: C2S/C2SElementCollectMsg.proto

package org.game.protobuf.c2s;

public final class C2SElementCollectMsg {
  private C2SElementCollectMsg() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface GetAllElementOrBuilder
      extends com.google.protobuf.MessageOrBuilder {
  }
  /**
   * Protobuf type {@code org.game.protobuf.c2s.GetAllElement}
   *
   * <pre>
   *获取所有图鉴
   *code = 4865
   * </pre>
   */
  public static final class GetAllElement extends
      com.google.protobuf.GeneratedMessage
      implements GetAllElementOrBuilder {
    // Use GetAllElement.newBuilder() to construct.
    private GetAllElement(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private GetAllElement(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final GetAllElement defaultInstance;
    public static GetAllElement getDefaultInstance() {
      return defaultInstance;
    }

    public GetAllElement getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private GetAllElement(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.game.protobuf.c2s.C2SElementCollectMsg.internal_static_org_game_protobuf_c2s_GetAllElement_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.game.protobuf.c2s.C2SElementCollectMsg.internal_static_org_game_protobuf_c2s_GetAllElement_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement.class, org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement.Builder.class);
    }

    public static com.google.protobuf.Parser<GetAllElement> PARSER =
        new com.google.protobuf.AbstractParser<GetAllElement>() {
      public GetAllElement parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new GetAllElement(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<GetAllElement> getParserForType() {
      return PARSER;
    }

    /**
     * Protobuf enum {@code org.game.protobuf.c2s.GetAllElement.MsgID}
     */
    public enum MsgID
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <code>eMsgID = 4865;</code>
       */
      eMsgID(0, 4865),
      ;

      /**
       * <code>eMsgID = 4865;</code>
       */
      public static final int eMsgID_VALUE = 4865;


      public final int getNumber() { return value; }

      public static MsgID valueOf(int value) {
        switch (value) {
          case 4865: return eMsgID;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<MsgID>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static com.google.protobuf.Internal.EnumLiteMap<MsgID>
          internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<MsgID>() {
              public MsgID findValueByNumber(int number) {
                return MsgID.valueOf(number);
              }
            };

      public final com.google.protobuf.Descriptors.EnumValueDescriptor
          getValueDescriptor() {
        return getDescriptor().getValues().get(index);
      }
      public final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptorForType() {
        return getDescriptor();
      }
      public static final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptor() {
        return org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement.getDescriptor().getEnumTypes().get(0);
      }

      private static final MsgID[] VALUES = values();

      public static MsgID valueOf(
          com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != getDescriptor()) {
          throw new java.lang.IllegalArgumentException(
            "EnumValueDescriptor is not for this type.");
        }
        return VALUES[desc.getIndex()];
      }

      private final int index;
      private final int value;

      private MsgID(int index, int value) {
        this.index = index;
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:org.game.protobuf.c2s.GetAllElement.MsgID)
    }

    private void initFields() {
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.game.protobuf.c2s.GetAllElement}
     *
     * <pre>
     *获取所有图鉴
     *code = 4865
     * </pre>
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElementOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.game.protobuf.c2s.C2SElementCollectMsg.internal_static_org_game_protobuf_c2s_GetAllElement_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.game.protobuf.c2s.C2SElementCollectMsg.internal_static_org_game_protobuf_c2s_GetAllElement_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement.class, org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement.Builder.class);
      }

      // Construct using org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.game.protobuf.c2s.C2SElementCollectMsg.internal_static_org_game_protobuf_c2s_GetAllElement_descriptor;
      }

      public org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement getDefaultInstanceForType() {
        return org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement.getDefaultInstance();
      }

      public org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement build() {
        org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement buildPartial() {
        org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement result = new org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement(this);
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement) {
          return mergeFrom((org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement other) {
        if (other == org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement.getDefaultInstance()) return this;
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.game.protobuf.c2s.C2SElementCollectMsg.GetAllElement) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      // @@protoc_insertion_point(builder_scope:org.game.protobuf.c2s.GetAllElement)
    }

    static {
      defaultInstance = new GetAllElement(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:org.game.protobuf.c2s.GetAllElement)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_org_game_protobuf_c2s_GetAllElement_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_org_game_protobuf_c2s_GetAllElement_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\036C2S/C2SElementCollectMsg.proto\022\025org.ga" +
      "me.protobuf.c2s\"%\n\rGetAllElement\"\024\n\005MsgI" +
      "D\022\013\n\006eMsgID\020\201&"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_org_game_protobuf_c2s_GetAllElement_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_org_game_protobuf_c2s_GetAllElement_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_org_game_protobuf_c2s_GetAllElement_descriptor,
              new java.lang.String[] { });
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
