package kozuma.shun.techacademy.routemapapp

import java.io.Serializable

class Friends(val friend_uid: String, val name: String, var sendBoolean: Boolean?, var recieveBoolean: Boolean?): Serializable
