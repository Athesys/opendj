/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at legal-notices/CDDLv1_0.txt
 * or http://forgerock.org/license/CDDLv1.0.html.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at legal-notices/CDDLv1_0.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Copyright 2009 Sun Microsystems, Inc.
 *      Portions Copyright 2016 ForgeRock AS.
 */

package org.forgerock.opendj.ldap.schema;

import static org.forgerock.opendj.ldap.schema.SchemaConstants.*;

import org.forgerock.i18n.LocalizableMessageBuilder;
import org.forgerock.opendj.ldap.ByteSequence;

/**
 * This class implements the protocol information attribute syntax, which is
 * being deprecated. As such, this implementation behaves exactly like the
 * directory string syntax.
 */
final class ProtocolInformationSyntaxImpl extends AbstractSyntaxImpl {

    @Override
    public String getApproximateMatchingRule() {
        return AMR_DOUBLE_METAPHONE_OID;
    }

    @Override
    public String getEqualityMatchingRule() {
        return EMR_CASE_IGNORE_OID;
    }

    @Override
    public String getName() {
        return SYNTAX_PROTOCOL_INFORMATION_NAME;
    }

    @Override
    public String getOrderingMatchingRule() {
        return OMR_CASE_IGNORE_OID;
    }

    @Override
    public String getSubstringMatchingRule() {
        return SMR_CASE_IGNORE_OID;
    }

    @Override
    public boolean isHumanReadable() {
        return true;
    }

    @Override
    public boolean valueIsAcceptable(final Schema schema, final ByteSequence value,
            final LocalizableMessageBuilder invalidReason) {
        // We will accept any value for this syntax.
        return true;
    }
}
