/*
 * Copyright 2015 Real Logic Ltd.
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
package uk.co.real_logic.fix_gateway.framer;

import uk.co.real_logic.fix_gateway.util.MilliClock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static uk.co.real_logic.fix_gateway.framer.SessionState.DISCONNECTED;

public abstract class AbstractSessionTest
{
    public static final long CONNECTION_ID = 3L;
    public static final long SESSION_ID = 2L;
    public static final long HEARTBEAT_INTERVAL = 2L;

    protected SessionProxy mockProxy = mock(SessionProxy.class);

    protected long currentTime = 0;
    protected MilliClock mockClock = () -> currentTime;

    public void verifyNoMessages()
    {
        verifyNoMoreInteractions(mockProxy);
    }

    public void verifyDisconnect()
    {
        verify(mockProxy).disconnect(CONNECTION_ID);
        assertState(DISCONNECTED);
    }

    public void assertState(final SessionState state)
    {
        assertEquals(state, session().state());
    }

    protected abstract Session session();
}
