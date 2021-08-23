package com.spit.lms.System.Event

import com.spit.lms.System.Base.BaseEvent

class ResponseEvent(var url: String, var response: Any) : BaseEvent()