package kozuma.shun.techacademy.routemapapp

import java.io.Serializable

class User(val name: String, val friends: ArrayList<Friends>, val latitude: String, val longitude: String, val uid: String, val senduser_id: String) : Serializable