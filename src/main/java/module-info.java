import se.uu.ub.cora.initialize.internal.InterfaceSpy;

module se.uu.ub.cora.initialize {
	requires transitive se.uu.ub.cora.logger;

	uses InterfaceSpy;

	exports se.uu.ub.cora.initialize;
}