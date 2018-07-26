# SPICES
SPICES – A particle-based Molecular Structure Line Notation and Support Library for Mesoscopic Simulation

SPICES (Simplified Particle Input ConnEction Specification) is a particle-based molecular structure representation derived from straightforward simplifications of the atom-based SMILES line notation. It aims at supporting tedious and error-prone molecular structure definitions for particle-based mesoscopic simulation techniques like Dissipative Particle Dynamics by allowing for an interplay of different molecular encoding levels that range from topological line notations and corresponding particle-graph visualizations to 3D structures with support of their spatial mapping into a simulation box.
An open Java library for SPICES structure handling and mesoscopic simulation support in combination with an open Java Graphical User Interface viewer application for visual topological inspection of SPICES definitions are provided.

SPICES uses the [GraphStream](http://graphstream-project.org/) and [Apache Commons IO](http://commons.apache.org/proper/commons-io/) libraries and is published as open source under the GNU General Public License version 3. Spices.jar is a Java library for SPICES handling and mesoscopic simulation support. SpicesToGraphStream.jar is a connection library used by SpicesViewer.jar, a Java Graphical User Interface (GUI) viewer application for visual topological inspection and manipulation of SPICES molecule definitions. This repository contains the Java bytecode libraries (including the GraphStream and Apache Commons Lang libraries), a Windows OS installer for the SpicesViewer GUI application, all Javadoc HTML documentations and the Netbeans source code packages including Unit tests.

SPICES is currently under scientific review - additional information will be published soon.

# Acknowledgements
The support of [GNWI - Gesellschaft für naturwissenschaftliche Informatik mbH](http://www.gnwi.de) is gratefully acknowledged.
