package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.ClassicSolverFactory
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestNotProvable
import kotlin.test.Ignore
import kotlin.test.Test

class TestClassicNotProvable : TestNotProvable, SolverFactory by ClassicSolverFactory  {
    private val prototype = TestNotProvable.prototype(this)

    @Test
    override fun testNPTrue() {
        prototype.testNPTrue()
    }

    @Test
    override fun testNPCut() {
        prototype.testNPCut()
    }

    @Test
    override fun testNPCutFail() {
        prototype.testNPCutFail()
    }

    @Test
    override fun testNPEquals() {
        prototype.testNPEquals()
    }

    @Test
    override fun testNPNum() {
        prototype.testNPNum()
    }

    @Test
    override fun testNPX() {
        prototype.testNPX()
    }

    @Test
    override fun testOrNotCutFail() {
        prototype.testOrNotCutFail()
    }
}