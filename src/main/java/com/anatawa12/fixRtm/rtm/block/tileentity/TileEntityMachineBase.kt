/// Copyright (c) 2021 anatawa12 and other contributors
/// This file is/was part of fixRTM, released under GNU LGPL v3 with few exceptions
/// See LICENSE at https://github.com/fixrtm/fixRTM for more details

package com.anatawa12.fixRtm.rtm.block.tileentity

import com.anatawa12.fixRtm.addEntityCrashInfoAboutModelSet
import jp.ngt.rtm.block.tileentity.TileEntityMachineBase
import net.minecraft.crash.CrashReportCategory

@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "unused")
fun TileEntityMachineBase.addInfoToCrashReport(category: CrashReportCategory) =
    addEntityCrashInfoAboutModelSet(category) { resourceState?.resourceSet?.config }
