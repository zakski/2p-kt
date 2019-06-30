package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [AtomImpl] and [Atom]
 *
 * @author Enrico
 */
internal class AtomImplTest {

    private val correctAtoms = AtomUtils.specialAtoms + AtomUtils.nonSpecialAtoms
    private val correctAtomInstances = correctAtoms.map(::AtomImpl)

    @Test
    fun functorCorrectness() {
        correctAtoms.zip(correctAtomInstances).forEach { (atomString, atomInstance) ->
            assertEquals(atomString, atomInstance.functor)
        }
    }

    @Test
    fun atomFunctorAndValueAreTheSame() {
        correctAtomInstances.forEach(AtomUtils::assertSameValueAndFunctor)
    }

    @Test
    fun noArguments() {
        correctAtomInstances.forEach(AtomUtils::assertNoArguments)
    }

    @Test
    fun zeroArity() {
        correctAtomInstances.forEach(AtomUtils::assertZeroArity)
    }

    @Test
    fun testNonSpecialAtomIsPropertiesAndTypes() {
        AtomUtils.nonSpecialAtoms.map(::AtomImpl)
                .forEach(TermTypeAssertionUtils::assertIsAtom)
    }

    @Test
    fun emptySetAtomDetected() {
        assertTrue(AtomImpl("{}").isEmptySet)
    }

    @Test
    fun emptyListAtomDetected() {
        assertTrue(AtomImpl("[]").isEmptyList)
    }

    @Test
    fun trueAtomDetected() {
        assertTrue(AtomImpl("true").isTrue)
    }

    @Test
    fun failAtomDetected() {
        assertTrue(AtomImpl("fail").isFail)
    }

    @Test
    fun strictlyEqualsWorksAsExpected() {
        val trueStruct = StructImpl("true", emptyArray())
        val trueAtom = AtomImpl("true")
        val trueTruth = Truth.`true`()

        // TODO review this behaviour, this is maybe incorrect
        assertTrue(trueStruct strictlyEquals trueAtom)
        assertFalse(trueAtom strictlyEquals trueStruct)

        assertTrue(trueAtom strictlyEquals trueTruth)
        assertTrue(trueTruth strictlyEquals trueAtom)
    }

    @Test
    fun structurallyEqualsWorksAsExpected() {
        val trueStruct = StructImpl("true", emptyArray())
        val trueAtom = AtomImpl("true")
        val trueTruth = Truth.`true`()

        assertTrue(trueStruct structurallyEquals trueAtom)
        assertTrue(trueAtom structurallyEquals trueStruct)

        assertTrue(trueAtom structurallyEquals trueTruth)
        assertTrue(trueTruth structurallyEquals trueAtom)
    }

    @Test
    fun atomFreshCopyShouldReturnTheInstanceItself() {
        correctAtomInstances.forEach(AtomUtils::assertFreshCopyIsItself)
    }
}
