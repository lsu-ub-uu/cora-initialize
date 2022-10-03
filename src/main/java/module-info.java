import se.uu.ub.cora.initialize.internal.InterfaceClassSpy;

module se.uu.ub.cora.initialize {
	requires transitive se.uu.ub.cora.logger;

	uses InterfaceClassSpy;

	exports se.uu.ub.cora.initialize;
}