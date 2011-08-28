## THIS CODE CAN'T CURRENTLY BE BUILT OR RUN.

It needs a volunteer to convert it into a working extension.  The primitives are currently written in the style used by built-in NetLogo primitives, and need to be changed to use the extensions API instead.  Want to tackle it?

## Description of primitives in src directory

__in-network-radius
syntax:  TURTLESET __in-network-radius RADIUS LINKSET
example:  ask one-of bankers [ show other bankers __in-network-radius
5 friendships ]

__network-distance
syntax: __network-distance TURTLE LINK-SET
example:  ask one-of-bankers [ show __network-distance the-best-banker
friendships ]

__network-shortest-path-links
syntax:  __network-shortest-path-links TURTLE LINK-SET
example: ask banker1 [ show __network-shortest-path-links banker2 friendships ]
->   [(link 2 3) (link 3 1)]  ; e.g. a list of the links along the
shortest path between.

__network-shortest-path-nodes
syntax: __network-shortest-path-nodes TURTLE LINK-SET
example: ask banker1 [ show __network-shortest-path-nodes banker2 friendships ]
->   [(banker 1) (banker 3) (banker 2)]  ; e.g. a list of the nodes
along the shortest path between.
 
Note that it is somewhat of an open question how the __network-distance primitive should handle distances between two nodes that are not reachable via the network.  Also, although these reporters were intended to support both directed and undirected networks, I'm not positive that they do in all cases.  Also, path lengths are computed based solely on the number of hops, and there currently isn't any way to specify a "weight/distance" variable for the links.

__average-path-length

## Description of primitives in src-more directory

__create-network-preferential
syntax:  __create-network-preferential TURTLESET LINK-BREED AVG-DEGREE
example:  __create-network-preferential bankers friendships 3
 (note that you should create the banker turtles ahead of time - this
primitive just creates a BA preferential attachment model network
between the turtles you specify.

__layout-magspring

__layout-quick

__layout-sphere

## Credits

This code was originally written by Forrest Stonedahl.

## Terms of Use

All contents © 2007–2011 Uri Wilensky

The contents of this package may be freely copied, distributed, altered, or otherwise used by anyone for any legal purpose.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
