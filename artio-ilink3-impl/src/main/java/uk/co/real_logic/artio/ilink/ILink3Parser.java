/*
 * Copyright 2020 Monotonic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.real_logic.artio.ilink;

import iLinkBinary.*;
import org.agrona.DirectBuffer;

import static uk.co.real_logic.artio.ilink.SimpleOpenFramingHeader.SOFH_LENGTH;

public class ILink3Parser extends AbstractILink3Parser
{
    private final MessageHeaderDecoder header = new MessageHeaderDecoder();
    private final NegotiationResponse501Decoder negotiationResponse = new NegotiationResponse501Decoder();
    private final NegotiationReject502Decoder negotiationReject = new NegotiationReject502Decoder();
    private final EstablishmentAck504Decoder establishmentAck = new EstablishmentAck504Decoder();
    private final EstablishmentReject505Decoder establishmentReject = new EstablishmentReject505Decoder();
    private final Terminate507Decoder terminate = new Terminate507Decoder();
    private final ILink3EndpointHandler handler;

    public ILink3Parser(final ILink3EndpointHandler handler)
    {
        this.handler = handler;
    }

    public int templateId(final DirectBuffer buffer, final int offset)
    {
        header.wrap(buffer, offset);
        return header.templateId();
    }

    public long onMessage(final DirectBuffer buffer, final int start)
    {
        final int offset = start + SOFH_LENGTH;

        header.wrap(buffer, offset);
        final int blockLength = header.blockLength();
        final int version = header.version();

        switch (header.templateId())
        {
            case NegotiationResponse501Decoder.TEMPLATE_ID:
            {
                return onNegotiationResponse(buffer, offset, blockLength, version);
            }

            case NegotiationReject502Decoder.TEMPLATE_ID:
            {
                return onNegotiationReject(buffer, offset, blockLength, version);
            }

            case EstablishmentAck504Decoder.TEMPLATE_ID:
            {
                return onEstablishmentAck(buffer, offset, blockLength, version);
            }

            case EstablishmentReject505Decoder.TEMPLATE_ID:
            {
                return onEstablishmentReject(buffer, offset, blockLength, version);
            }

            case Terminate507Decoder.TEMPLATE_ID:
            {
                return onTerminate(buffer, offset, blockLength, version);
            }
        }
        return 1;
    }

    private long onNegotiationResponse(
        final DirectBuffer buffer, final int offset, final int blockLength, final int version)
    {
        negotiationResponse.wrap(buffer, offset, blockLength, version);
        return handler.onNegotiationResponse(
            negotiationResponse.uUID(),
            negotiationResponse.requestTimestamp(),
            negotiationResponse.secretKeySecureIDExpiration(),
            // negotiationResponse.faultToleranceIndicator()
            // negotiationResponse.splitMsg()
            negotiationResponse.previousSeqNo(),
            negotiationResponse.previousUUID());
    }

    private long onNegotiationReject(
        final DirectBuffer buffer, final int offset, final int blockLength, final int version)
    {
        negotiationReject.wrap(buffer, offset, blockLength, version);
        return handler.onNegotiationReject(
            negotiationReject.reason(),
            negotiationReject.uUID(),
            negotiationReject.requestTimestamp(),
            negotiationReject.errorCodes());
            // negotiationResponse.faultToleranceIndicator()
            // negotiationResponse.splitMsg());
    }

    private long onEstablishmentAck(
        final DirectBuffer buffer, final int offset, final int blockLength, final int version)
    {
        establishmentAck.wrap(buffer, offset, blockLength, version);
        return handler.onEstablishmentAck(
            establishmentAck.uUID(),
            establishmentAck.requestTimestamp(),
            establishmentAck.nextSeqNo(),
            establishmentAck.previousSeqNo(),
            establishmentAck.previousUUID(),
            establishmentAck.keepAliveInterval(),
            establishmentAck.secretKeySecureIDExpiration());
            // establishmentAck.faultToleranceIndicator()
            // establishmentAck.splitMsg()
    }

    private long onEstablishmentReject(
        final DirectBuffer buffer, final int offset, final int blockLength, final int version)
    {
        establishmentReject.wrap(buffer, offset, blockLength, version);
        return handler.onEstablishmentReject(
            establishmentReject.reason(),
            establishmentReject.uUID(),
            establishmentReject.requestTimestamp(),
            establishmentReject.nextSeqNo(),
            establishmentReject.errorCodes());
        // establishmentReject.faultToleranceIndicator()
        // establishmentReject.splitMsg()
    }

    private long onTerminate(
        final DirectBuffer buffer, final int offset, final int blockLength, final int version)
    {
        terminate.wrap(buffer, offset, blockLength, version);
        return handler.onTerminate(
            terminate.reason(),
            terminate.uUID(),
            terminate.requestTimestamp(),
            terminate.errorCodes());
            // terminate.splitMsg()
    }
}