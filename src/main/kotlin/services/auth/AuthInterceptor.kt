package services.auth

import domain.auth.ResultCode
import io.grpc.Context
import io.grpc.Contexts
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.Status

class AuthInterceptor(private val authenticator: Authenticator) : ServerInterceptor {

    companion object {
        val LOGIN_CONTEXT_KEY: Context.Key<String> = Context.key("user")
    }

    override fun <ReqT, RespT> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        val methodName = call.methodDescriptor.fullMethodName

        if (methodName == "AuthService/login" || methodName == "AuthService/register") {
            return next.startCall(call, headers)
        }

        val login = headers.get(Metadata.Key.of("login-bin", Metadata.BINARY_BYTE_MARSHALLER))
        val password = headers.get(Metadata.Key.of("password-bin", Metadata.BINARY_BYTE_MARSHALLER))

        if (login == null || password == null) {
            call.close(Status.INVALID_ARGUMENT.withDescription("Not enough data were passed"), headers)
            return object : ServerCall.Listener<ReqT>() {}
        }

        val result = authenticator.login(login.toString(), password.toString())
        if (result != ResultCode.OPERATION_SUCCESS) {
            call.close(Status.UNAUTHENTICATED.withDescription("Invalid credentials"), headers)
            return object : ServerCall.Listener<ReqT>() {}
        }

        val context = Context.current().withValue(LOGIN_CONTEXT_KEY, login.toString())

        return Contexts.interceptCall(context, call, headers, next)
    }
}