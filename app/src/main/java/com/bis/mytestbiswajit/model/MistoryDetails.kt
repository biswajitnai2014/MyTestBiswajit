package com.bis.Expence.data.model

import com.google.gson.annotations.SerializedName

data class MistoryDetails(
    @SerializedName("mistryDataValue" ) var mistryDataValue : ArrayList<MistryDataValue> = arrayListOf()

)




data class MistryDataValue (

    @SerializedName("mDate"      ) var mDate      : String? = null,
    @SerializedName("things"     ) var things     : String? = null,
    @SerializedName("quentity"   ) var quentity   : String? = null,
    @SerializedName("billNumber" ) var billNumber : String? = null,
    @SerializedName("price"      ) var price      : String? = null,
    @SerializedName("mBaki"      ) var mBaki      : String? = null,
    @SerializedName("mRemark"    ) var mRemark    : String? = null,
    @SerializedName("mStatus"    ) var mStatus    : String? = null

)
