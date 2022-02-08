package api

import api.data.manga.Description
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


class DescriptionAdapter : JsonAdapter<Description>() {
    var moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    var jsonAdapter: JsonAdapter<Description> = moshi.adapter(Description::class.java)

    @ToJson override fun toJson(writer: JsonWriter, value: Description?) {
        return jsonAdapter.toJson(writer, value)
    }

    @FromJson override fun fromJson(reader: JsonReader): Description {
        val peeked = reader.peekJson()
        try {
            peeked.beginObject()
            return jsonAdapter.fromJson(reader)!!
        } catch (ignored: JsonDataException ) {
            reader.skipValue()
        } catch (ignored : NullPointerException){}
        return Description("")
    }
}