//package com.codewithajaj.ecommerce
//
//import com.google.gson.annotations.SerializedName
//data class DataItem(
//	@SerializedName("country") val country: String? = null,
//	@SerializedName("shipping_address_id") val shippingAddressId: String? = null,
//	@SerializedName("city") val city: String? = null,
//	@SerializedName("created_at") val createdAt: String? = null,
//	@SerializedName("address_line2") val addressLine2: String? = null,
//	@SerializedName("updated_at") val updatedAt: String? = null,
//	@SerializedName("user_id") val userId: String? = null,
//	@SerializedName("address_line1") val addressLine1: String? = null,
//	@SerializedName("alternate_phone_number") val alternatePhoneNumber: String? = null,
//	@SerializedName("phone_number") val phoneNumber: String? = null,
//	@SerializedName("state") val state: String? = null,
//	@SerializedName("recipient_name") val recipientName: String? = null,
//	@SerializedName("postal_code") val postalCode: String? = null
//)
//
//data class GetAddressResponse(
//	val data: List<DataItem?>? = null,
//	val status: String? = null
//)
//
package com.codewithajaj.ecommerce

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class DataItem(
	@SerializedName("country") val country: String? = null,
	@SerializedName("shipping_address_id") val shippingAddressId: String? = null,
	@SerializedName("city") val city: String? = null,
	@SerializedName("created_at") val createdAt: String? = null,
	@SerializedName("address_line2") val addressLine2: String? = null,
	@SerializedName("updated_at") val updatedAt: String? = null,
	@SerializedName("user_id") val userId: String? = null,
	@SerializedName("address_line1") val addressLine1: String? = null,
	@SerializedName("alternate_phone_number") val alternatePhoneNumber: String? = null,
	@SerializedName("phone_number") val phoneNumber: String? = null,
	@SerializedName("state") val state: String? = null,
	@SerializedName("recipient_name") val recipientName: String? = null,
	@SerializedName("postal_code") val postalCode: String? = null
) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(country)
		parcel.writeString(shippingAddressId)
		parcel.writeString(city)
		parcel.writeString(createdAt)
		parcel.writeString(addressLine2)
		parcel.writeString(updatedAt)
		parcel.writeString(userId)
		parcel.writeString(addressLine1)
		parcel.writeString(alternatePhoneNumber)
		parcel.writeString(phoneNumber)
		parcel.writeString(state)
		parcel.writeString(recipientName)
		parcel.writeString(postalCode)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<DataItem> {
		override fun createFromParcel(parcel: Parcel): DataItem {
			return DataItem(parcel)
		}

		override fun newArray(size: Int): Array<DataItem?> {
			return arrayOfNulls(size)
		}
	}
}

data class GetAddressResponse(
	val data: List<DataItem?>? = null,
	val status: String? = null
)
