package org.lume.extension.multiplayer.protocol.server;

import java.io.IOException;

import org.lume.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.lume.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.lume.extension.multiplayer.protocol.shared.Connection;
import org.lume.extension.multiplayer.protocol.shared.IMessageHandler;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 21:02:16 - 19.09.2009
 */
public interface IClientMessageHandler<C extends Connection> extends IMessageHandler<C, ClientConnector<C>, IClientMessage> {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	@Override
	public void onHandleMessage(final ClientConnector<C> pClientConnector, final IClientMessage pClientMessage) throws IOException;
}