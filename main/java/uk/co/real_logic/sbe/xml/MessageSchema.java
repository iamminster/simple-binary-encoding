/* -*- mode: java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil -*- */
/*
 * Copyright 2013 Real Logic Ltd.
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
package uk.co.real_logic.sbe.xml;

import org.w3c.dom.Node;
import uk.co.real_logic.sbe.util.Verify;

import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Map;

import static uk.co.real_logic.sbe.xml.XmlSchemaParser.*;

/**
 * Class to encapsulate the message schema attributes, messageHeader, and reference for multiple {@link Message} objects
 */
public class MessageSchema
{
    private static final String MESSAGE_HEADER_KEY = "messageHeader";

    private final String pkg;                         // package (optional?)
    private final String description;                 // description (optional)
    private final long version;                       // version (optional - default is 0)
    private final String semanticVersion;             // semanticVersion (optional)
    private final ByteOrder byteOrder;                // byteOrder (optional - default is littleEndian)
    private final Map<String, Type> typeByNameMap;
    private final Map<Long, Message> messageByIdMap;

    public MessageSchema(final Node schemaNode,
                         final Map<String, Type> typeByNameMap,
                         final Map<Long, Message> messageByIdMap)
    {
        Verify.present(typeByNameMap, MESSAGE_HEADER_KEY, "Message header");

        this.pkg = getAttributeValue(schemaNode, "package");
        this.description = getAttributeValueOrNull(schemaNode, "description");
        this.version = Long.parseLong(getAttributeValue(schemaNode, "version", "0"));  // default version is 0
        this.semanticVersion = getMultiNamedAttributeValueOrNull(schemaNode, new String[]{"semanticVersion", "fixVersion"});
        this.byteOrder = lookupByteOrder(getAttributeValue(schemaNode, "byteOrder", "littleEndian"));
        this.typeByNameMap = typeByNameMap;
        this.messageByIdMap = messageByIdMap;
    }

    /**
     * @return the Schema messageHeader type or null if not defined. This should be a {@link CompositeType}.
     */
    public CompositeType getMessageHeader()
    {
        return (CompositeType)typeByNameMap.get(MESSAGE_HEADER_KEY);
    }

    public String getPackage()
    {
        return pkg;
    }

    public String getDescription()
    {
        return description;
    }

    public long getVersion()
    {
        return version;
    }

    public String getSemanticVersion()
    {
        return semanticVersion;
    }

    /**
     * Return a given {@link Message} object with the given schemaId.
     *
     * @param schemaId of the message to return.
     * @return a given {@link Message} for the schemaId.
     */
    public Message getMessage(final long schemaId)
    {
        return messageByIdMap.get(Long.valueOf(schemaId));
    }

    /**
     * Get the {@link Collection} of {@link Message}s for this Schema.
     *
     * @return the {@link Collection} of {@link Message}s for this Schema.
     */
    public Collection<Message> getMessages()
    {
        return messageByIdMap.values();
    }

    /**
     * Return the byte order specified by the messageSchema
     */
    public ByteOrder getByteOrder()
    {
        return byteOrder;
    }
}
