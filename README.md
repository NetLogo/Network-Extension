## THIS CODE CAN'T CURRENTLY BE BUILT OR RUN (EXCEPT AT NORTHWESTERN)

It requires NetLogo 5.0beta5, which isn't out yet.

## TO DO

* have Forrest review the design and the code
* get README in final shape for release

## Notes

Anywhere a link breed is required, `links` is also accepted.

Path lengths are computed based solely on the number of hops.  There
isn't currently any way to specify a "weight" or "distance" variable
for links.

Ideally, instead of taking a link breed as input, the breed could take
the place of `link` in the primitive name.  Currently the extensions
API doesn't allow primitives that change name like this, but it should
in some future NetLogo version.  Anyway, this is why all the names
have `link` in them.

## Primitives

### network:in-link-radius, network:in-out-link-radius, network:in-in-link-radius

![turtle](https://github.com/NetLogo/Network-Extension/raw/master/turtle.gif) `TURTLESET network:in-link-radius RADIUS LINK-BREED`  
![turtle](https://github.com/NetLogo/Network-Extension/raw/master/turtle.gif) `TURTLESET network:in-out-link-radius RADIUS LINK-BREED`  
![turtle](https://github.com/NetLogo/Network-Extension/raw/master/turtle.gif) `TURTLESET network:in-in-link-radius RADIUS LINK-BREED`

example: `ask one-of bankers [ show other bankers in-network-radius 5 friendships ]`

Returns the set of turtles within the given distance (number of links followed)
of the calling turtle.
Searches breadth-first from the calling turtle,
following links of the given link breed.

The `in-link-radius` form works with undirected links.  The other two
forms work with directed links; `out` or `in` specifies whether links
are followed in the normal direction (`out`), or in reverse (`in`).

### network:link-distance

![turtle](https://github.com/NetLogo/Network-Extension/raw/master/turtle.gif) `network:link-distance TURTLE LINK-BREED`

example: `ask one-of-bankers [ show network:link-distance the-best-banker friendships ]`

Finds the distance to the destination turtle (number of links followed).
Searches breadth-first from the calling turtle,
following links of the given link breed.

Reports false if no path exists.

### network:link-path, link-path-turtles

![turtle](https://github.com/NetLogo/Network-Extension/raw/master/turtle.gif) `network:link-path TURTLE LINK-BREED`
![turtle](https://github.com/NetLogo/Network-Extension/raw/master/turtle.gif) `network:link-path-turtles TURTLE LINK-BREED`  

example: `ask banker1 [ show network:link-path banker3 friendships ]`
->   [(link 1 2) (link 2 3)]

example:`ask banker1 [ show network:link-path-turtles banker3 friendships ]`
->   [(banker 1) (banker 2) (banker 3)]
 
Reports a list of turtles or links following the shortest path from the calling
turtle to the destination turtle.

Reports an empty list if no path exists.

If `network:link-path-turtles` is used, the calling turtle and the
destination are included in the list.

Searches breadth-first from the calling turtle,
following links of the given link breed.

Follows links at the same depth in random order.  If there are
multiple shortest paths, a different path may be returned on
subsequent calls, depending on the random choices made during search.

### network:mean-link-path-length

`network:mean-link-path-length TURTLE-SET LINK-BREED`

Reports the average shortest-path length between all distinct pairs of
nodes in the given set of turtles, following links of the given link
breed.

Reports false unless paths exist between all pairs.

## Transition guide

### Renamed primitives

The primitives in this extension were present in NetLogo 4.1, but with different names.
They were renamed as follows:

* `__network-distance` to `network:link-distance`
* `__in-network-radius` to `network:in-link-radius`
* `__average-path-length` to `network:mean-link-path-length`
* `__network-shortest-path-turtles` to `network:link-path-turtles`
* `__network-shortest-path-links` to `network:link-path`

### Omitted primitives

The following primitives, present in NetLogo 4.1 but not NetLogo 5.0, are not included in this extension either:

* `__create-network-preferential`
* `__layout-magspring`
* `__layout-quick`
* `__layout-sphere`

For the source code for these primitives, see [this commit](https://github.com/NetLogo/Network-Extension/commit/eea275e20b5c2a76fc76b8b7642d2a5e7df0a1e4).  But note they are written in the style used by built-in NetLogo primitives. To be brought back to life, they'd need to be changed to use the extensions API instead.

## Credits

The first versions of these primitives were written by Forrest Stonedahl.

## Terms of Use

All contents © 2007–2011 Uri Wilensky

The contents of this package may be freely copied, distributed, altered, or otherwise used by anyone for any legal purpose.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
