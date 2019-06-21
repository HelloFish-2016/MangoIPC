// IRemoteService.aidl
package com.mango.ipcore;

import com.mango.ipcore.IPCRequest;
import com.mango.ipcore.IPCResponse;

interface IRemoteService {
    IPCResponse sendRequest(in IPCRequest request);
}
