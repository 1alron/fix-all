package io.alron.fixall.presentation.util

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class UiTextTest {

    @Test
    fun `DynamicString returns correct value`() {
        val message = "Hello World"
        val uiText = UiText.DynamicString(message)
        
        val context = mockk<Context>()
        assertEquals(message, uiText.asString(context))
    }

    @Test
    fun `StringResource returns correct value from context`() {
        val resId = 123
        val expectedString = "Resource String"
        val context = mockk<Context>()
        
        every { context.getString(resId) } returns expectedString
        every { context.getString(resId, *anyVararg()) } returns expectedString
        
        val uiText = UiText.StringResource(resId)
        assertEquals(expectedString, uiText.asString(context))
    }

    @Test
    fun `StringResource with arguments returns formatted value from context`() {
        val resId = 123
        val arg = "Admin"
        val expectedString = "Hello Admin"
        val context = mockk<Context>()
        
        every { context.getString(resId, any()) } returns expectedString
        every { context.getString(resId, *anyVararg()) } returns expectedString
        
        val uiText = UiText.StringResource(resId, arg)
        assertEquals(expectedString, uiText.asString(context))
    }
}
