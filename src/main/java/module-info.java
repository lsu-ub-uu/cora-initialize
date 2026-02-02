import se.uu.ub.cora.initialize.example.SelectOrderExample;
import se.uu.ub.cora.initialize.example.SelectTypeExample;

module se.uu.ub.cora.initialize {
	requires transitive se.uu.ub.cora.logger;

	uses SelectOrderExample;
	uses SelectTypeExample;

	exports se.uu.ub.cora.initialize;
}