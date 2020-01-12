package com.justai.aimybox.assistant

import com.justai.aimybox.Aimybox
import com.justai.aimybox.core.CustomSkill
import com.justai.aimybox.dialogapi.command.CommandRequest
import com.justai.aimybox.dialogapi.command.CommandResponse
import com.justai.aimybox.model.Response
import com.justai.aimybox.model.TextSpeech

class EchoSkill: CustomSkill<CommandRequest, CommandResponse> {

    override fun canHandle(response: CommandResponse) = response.commandId == R.id.cmd_echo

    override suspend fun onResponse(
        response: CommandResponse,
        aimybox: Aimybox,
        defaultHandler: suspend (Response) -> Unit
    ) {
        aimybox.speak(TextSpeech(response.query!!))
    }
}