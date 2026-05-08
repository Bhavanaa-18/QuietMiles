package com.example.drivingapp.ui.contacts

import androidx.lifecycle.ViewModel
import com.example.drivingapp.data.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ContactListUiState(
    val contacts: List<Contact> = listOf(
        Contact(1, "Alice Johnson", "+1-555-0101"),
        Contact(2, "Bob Smith", "+1-555-0102"),
        Contact(3, "Carol White", "+1-555-0103")
    )
)

class ContactListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ContactListUiState())
    val uiState: StateFlow<ContactListUiState> = _uiState.asStateFlow()

    fun addContact(name: String, phone: String) {
        val newContact = Contact(
            id = (_uiState.value.contacts.maxOfOrNull { it.id } ?: 0) + 1,
            name = name,
            phoneNumber = phone
        )
        _uiState.update { it.copy(contacts = it.contacts + newContact) }
    }

    fun deleteContact(contact: Contact) {
        _uiState.update { it.copy(contacts = it.contacts - contact) }
    }
}