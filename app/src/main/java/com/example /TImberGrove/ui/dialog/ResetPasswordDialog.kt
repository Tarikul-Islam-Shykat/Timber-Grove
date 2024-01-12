package com.example.timbertrove.ui.Dialog

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.timbertrove.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


fun Fragment.setUpBottomSheetDialog(
    onSendClick : (String) -> Unit
){
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED // this is for showing also the button.
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.edtResetPasswordMail)
    val buttonSend = view.findViewById<Button>(R.id.btn_resetEmailSend)
    val buttonCancel = view.findViewById<Button>(R.id.btnBottomSheetCancel)

    buttonSend.setOnClickListener {
        val email = edEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }

    buttonCancel.setOnClickListener {
        dialog.dismiss()
    }

}
