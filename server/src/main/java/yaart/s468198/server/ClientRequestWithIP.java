package yaart.s468198.server;

import yaart.s468198.base.response.ClientCommandRequest;
import java.net.InetSocketAddress;

/**
 * ClientResponseWithIP - дата класс для хранения ip клиента и его запроса
 * @param clientResponse
 */
public record ClientRequestWithIP(ClientCommandRequest clientResponse, InetSocketAddress address) {}