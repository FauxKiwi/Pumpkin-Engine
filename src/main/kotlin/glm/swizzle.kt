package glm

///////////////////////////////////////////////////////////////////////////
// Vec2
///////////////////////////////////////////////////////////////////////////
var <T> Vec2T<T>.xy get() = Vec2T(x, y); set(v) { x = v.x; y = v.y }
var <T> Vec2T<T>.yx get() = Vec2T(y, x); set(v) { x = v.y; y = v.x }

var <T> Vec2T<T>.xx get() = Vec2T(x, y); set(v) { x = v.x; y = v.y }
var <T> Vec2T<T>.yy get() = Vec2T(y, x); set(v) { x = v.y; y = v.x }

///////////////////////////////////////////////////////////////////////////
// Vec3
///////////////////////////////////////////////////////////////////////////
var <T> Vec3T<T>.xy get() = Vec2T(x, y); set(v) { x = v.x; y = v.y }
var <T> Vec3T<T>.yx get() = Vec2T(y, x); set(v) { x = v.y; y = v.x }

var <T> Vec3T<T>.xx get() = Vec2T(x, y); set(v) { x = v.x; y = v.y }
var <T> Vec3T<T>.yy get() = Vec2T(y, x); set(v) { x = v.y; y = v.x }

var <T> Vec3T<T>.xyz get() = Vec3T(x, y, z); set(v) { x = v.x; y = v.y; z = v.z }
var <T> Vec3T<T>.xzy get() = Vec3T(x, z, y); set(v) { x = v.x; y = v.z; z = v.y }
var <T> Vec3T<T>.yxz get() = Vec3T(y, x, z); set(v) { x = v.y; y = v.x; z = v.z }
var <T> Vec3T<T>.yzx get() = Vec3T(y, z, x); set(v) { x = v.y; y = v.z; z = v.x }
var <T> Vec3T<T>.zxy get() = Vec3T(z, x, y); set(v) { x = v.z; y = v.x; z = v.y }
var <T> Vec3T<T>.zyx get() = Vec3T(z, y, x); set(v) { x = v.z; y = v.y; z = v.x }

var <T> Vec3T<T>.xxy get() = Vec3T(x, x, y); set(v) { x = v.x; y = v.x; z = v.y }
var <T> Vec3T<T>.xxz get() = Vec3T(x, x, z); set(v) { x = v.x; y = v.x; z = v.z }
var <T> Vec3T<T>.yyx get() = Vec3T(y, y, x); set(v) { x = v.y; y = v.y; z = v.x }
var <T> Vec3T<T>.yyz get() = Vec3T(y, y, z); set(v) { x = v.y; y = v.y; z = v.z }
var <T> Vec3T<T>.zzx get() = Vec3T(z, z, x); set(v) { x = v.z; y = v.z; z = v.x }
var <T> Vec3T<T>.zzy get() = Vec3T(z, z, y); set(v) { x = v.z; y = v.z; z = v.y }

var <T> Vec3T<T>.xxx get() = Vec3T(x, x, x); set(v) { x = v.x; y = v.x; z = v.x }
var <T> Vec3T<T>.yyy get() = Vec3T(y, y, y); set(v) { x = v.y; y = v.y; z = v.y }
var <T> Vec3T<T>.zzz get() = Vec3T(z, z, z); set(v) { x = v.z; y = v.z; z = v.z }

///////////////////////////////////////////////////////////////////////////
// Vec4
///////////////////////////////////////////////////////////////////////////
var <T> Vec4T<T>.xy get() = Vec2T(x, y); set(v) { x = v.x; y = v.y }
var <T> Vec4T<T>.yx get() = Vec2T(y, x); set(v) { x = v.y; y = v.x }

var <T> Vec4T<T>.xx get() = Vec2T(x, y); set(v) { x = v.x; y = v.y }
var <T> Vec4T<T>.yy get() = Vec2T(y, x); set(v) { x = v.y; y = v.x }

var <T> Vec4T<T>.xyz get() = Vec3T(x, y, z); set(v) { x = v.x; y = v.y; z = v.z }
var <T> Vec4T<T>.xzy get() = Vec3T(x, z, y); set(v) { x = v.x; y = v.z; z = v.y }
var <T> Vec4T<T>.yxz get() = Vec3T(y, x, z); set(v) { x = v.y; y = v.x; z = v.z }
var <T> Vec4T<T>.yzx get() = Vec3T(y, z, x); set(v) { x = v.y; y = v.z; z = v.x }
var <T> Vec4T<T>.zxy get() = Vec3T(z, x, y); set(v) { x = v.z; y = v.x; z = v.y }
var <T> Vec4T<T>.zyx get() = Vec3T(z, y, x); set(v) { x = v.z; y = v.y; z = v.x }

var <T> Vec4T<T>.xxy get() = Vec3T(x, x, y); set(v) { x = v.x; y = v.x; z = v.y }
var <T> Vec4T<T>.xxz get() = Vec3T(x, x, z); set(v) { x = v.x; y = v.x; z = v.z }
var <T> Vec4T<T>.yyx get() = Vec3T(y, y, x); set(v) { x = v.y; y = v.y; z = v.x }
var <T> Vec4T<T>.yyz get() = Vec3T(y, y, z); set(v) { x = v.y; y = v.y; z = v.z }
var <T> Vec4T<T>.zzx get() = Vec3T(z, z, x); set(v) { x = v.z; y = v.z; z = v.x }
var <T> Vec4T<T>.zzy get() = Vec3T(z, z, y); set(v) { x = v.z; y = v.z; z = v.y }

var <T> Vec4T<T>.xxx get() = Vec3T(x, x, x); set(v) { x = v.x; y = v.x; z = v.x }
var <T> Vec4T<T>.yyy get() = Vec3T(y, y, y); set(v) { x = v.y; y = v.y; z = v.y }
var <T> Vec4T<T>.zzz get() = Vec3T(z, z, z); set(v) { x = v.z; y = v.z; z = v.z }

var <T> Vec4T<T>.xxxx get() = Vec4T(x, x, x, x); set(v) { x = v.x; y = v.x; z = v.x; w = v.x }
var <T> Vec4T<T>.xxxy get() = Vec4T(x, x, x, y); set(v) { x = v.x; y = v.x; z = v.x; w = v.y }
var <T> Vec4T<T>.xxxz get() = Vec4T(x, x, x, z); set(v) { x = v.x; y = v.x; z = v.x; w = v.z }
var <T> Vec4T<T>.xxxw get() = Vec4T(x, x, x, w); set(v) { x = v.x; y = v.x; z = v.x; w = v.w }
var <T> Vec4T<T>.xxyx get() = Vec4T(x, x, y, x); set(v) { x = v.x; y = v.x; z = v.y; w = v.x }
var <T> Vec4T<T>.xxyy get() = Vec4T(x, x, y, y); set(v) { x = v.x; y = v.x; z = v.y; w = v.y }
var <T> Vec4T<T>.xxyz get() = Vec4T(x, x, y, z); set(v) { x = v.x; y = v.x; z = v.y; w = v.z }
var <T> Vec4T<T>.xxyw get() = Vec4T(x, x, y, w); set(v) { x = v.x; y = v.x; z = v.y; w = v.w }
var <T> Vec4T<T>.xxzx get() = Vec4T(x, x, z, x); set(v) { x = v.x; y = v.x; z = v.z; w = v.x }
var <T> Vec4T<T>.xxzy get() = Vec4T(x, x, z, y); set(v) { x = v.x; y = v.x; z = v.z; w = v.y }
var <T> Vec4T<T>.xxzz get() = Vec4T(x, x, z, z); set(v) { x = v.x; y = v.x; z = v.z; w = v.z }
var <T> Vec4T<T>.xxzw get() = Vec4T(x, x, z, w); set(v) { x = v.x; y = v.x; z = v.z; w = v.w }
var <T> Vec4T<T>.xxwx get() = Vec4T(x, x, w, x); set(v) { x = v.x; y = v.x; z = v.w; w = v.x }
var <T> Vec4T<T>.xxwy get() = Vec4T(x, x, w, y); set(v) { x = v.x; y = v.x; z = v.w; w = v.y }
var <T> Vec4T<T>.xxwz get() = Vec4T(x, x, w, z); set(v) { x = v.x; y = v.x; z = v.w; w = v.z }
var <T> Vec4T<T>.xxww get() = Vec4T(x, x, w, w); set(v) { x = v.x; y = v.x; z = v.w; w = v.w }
var <T> Vec4T<T>.xyxx get() = Vec4T(x, y, x, x); set(v) { x = v.x; y = v.y; z = v.x; w = v.x }
var <T> Vec4T<T>.xyxy get() = Vec4T(x, y, x, y); set(v) { x = v.x; y = v.y; z = v.x; w = v.y }
var <T> Vec4T<T>.xyxz get() = Vec4T(x, y, x, z); set(v) { x = v.x; y = v.y; z = v.x; w = v.z }
var <T> Vec4T<T>.xyxw get() = Vec4T(x, y, x, w); set(v) { x = v.x; y = v.y; z = v.x; w = v.w }
var <T> Vec4T<T>.xyyx get() = Vec4T(x, y, y, x); set(v) { x = v.x; y = v.y; z = v.y; w = v.x }
var <T> Vec4T<T>.xyyy get() = Vec4T(x, y, y, y); set(v) { x = v.x; y = v.y; z = v.y; w = v.y }
var <T> Vec4T<T>.xyyz get() = Vec4T(x, y, y, z); set(v) { x = v.x; y = v.y; z = v.y; w = v.z }
var <T> Vec4T<T>.xyyw get() = Vec4T(x, y, y, w); set(v) { x = v.x; y = v.y; z = v.y; w = v.w }
var <T> Vec4T<T>.xyzx get() = Vec4T(x, y, z, x); set(v) { x = v.x; y = v.y; z = v.z; w = v.x }
var <T> Vec4T<T>.xyzy get() = Vec4T(x, y, z, y); set(v) { x = v.x; y = v.y; z = v.z; w = v.y }
var <T> Vec4T<T>.xyzz get() = Vec4T(x, y, z, z); set(v) { x = v.x; y = v.y; z = v.z; w = v.z }
var <T> Vec4T<T>.xyzw get() = Vec4T(x, y, z, w); set(v) { x = v.x; y = v.y; z = v.z; w = v.w }
var <T> Vec4T<T>.xywx get() = Vec4T(x, y, w, x); set(v) { x = v.x; y = v.y; z = v.w; w = v.x }
var <T> Vec4T<T>.xywy get() = Vec4T(x, y, w, y); set(v) { x = v.x; y = v.y; z = v.w; w = v.y }
var <T> Vec4T<T>.xywz get() = Vec4T(x, y, w, z); set(v) { x = v.x; y = v.y; z = v.w; w = v.z }
var <T> Vec4T<T>.xyww get() = Vec4T(x, y, w, w); set(v) { x = v.x; y = v.y; z = v.w; w = v.w }
var <T> Vec4T<T>.xzxx get() = Vec4T(x, z, x, x); set(v) { x = v.x; y = v.z; z = v.x; w = v.x }
var <T> Vec4T<T>.xzxy get() = Vec4T(x, z, x, y); set(v) { x = v.x; y = v.z; z = v.x; w = v.y }
var <T> Vec4T<T>.xzxz get() = Vec4T(x, z, x, z); set(v) { x = v.x; y = v.z; z = v.x; w = v.z }
var <T> Vec4T<T>.xzxw get() = Vec4T(x, z, x, w); set(v) { x = v.x; y = v.z; z = v.x; w = v.w }
var <T> Vec4T<T>.xzyx get() = Vec4T(x, z, y, x); set(v) { x = v.x; y = v.z; z = v.y; w = v.x }
var <T> Vec4T<T>.xzyy get() = Vec4T(x, z, y, y); set(v) { x = v.x; y = v.z; z = v.y; w = v.y }
var <T> Vec4T<T>.xzyz get() = Vec4T(x, z, y, z); set(v) { x = v.x; y = v.z; z = v.y; w = v.z }
var <T> Vec4T<T>.xzyw get() = Vec4T(x, z, y, w); set(v) { x = v.x; y = v.z; z = v.y; w = v.w }
var <T> Vec4T<T>.xzzx get() = Vec4T(x, z, z, x); set(v) { x = v.x; y = v.z; z = v.z; w = v.x }
var <T> Vec4T<T>.xzzy get() = Vec4T(x, z, z, y); set(v) { x = v.x; y = v.z; z = v.z; w = v.y }
var <T> Vec4T<T>.xzzz get() = Vec4T(x, z, z, z); set(v) { x = v.x; y = v.z; z = v.z; w = v.z }
var <T> Vec4T<T>.xzzw get() = Vec4T(x, z, z, w); set(v) { x = v.x; y = v.z; z = v.z; w = v.w }
var <T> Vec4T<T>.xzwx get() = Vec4T(x, z, w, x); set(v) { x = v.x; y = v.z; z = v.w; w = v.x }
var <T> Vec4T<T>.xzwy get() = Vec4T(x, z, w, y); set(v) { x = v.x; y = v.z; z = v.w; w = v.y }
var <T> Vec4T<T>.xzwz get() = Vec4T(x, z, w, z); set(v) { x = v.x; y = v.z; z = v.w; w = v.z }
var <T> Vec4T<T>.xzww get() = Vec4T(x, z, w, w); set(v) { x = v.x; y = v.z; z = v.w; w = v.w }
var <T> Vec4T<T>.xwxx get() = Vec4T(x, w, x, x); set(v) { x = v.x; y = v.w; z = v.x; w = v.x }
var <T> Vec4T<T>.xwxy get() = Vec4T(x, w, x, y); set(v) { x = v.x; y = v.w; z = v.x; w = v.y }
var <T> Vec4T<T>.xwxz get() = Vec4T(x, w, x, z); set(v) { x = v.x; y = v.w; z = v.x; w = v.z }
var <T> Vec4T<T>.xwxw get() = Vec4T(x, w, x, w); set(v) { x = v.x; y = v.w; z = v.x; w = v.w }
var <T> Vec4T<T>.xwyx get() = Vec4T(x, w, y, x); set(v) { x = v.x; y = v.w; z = v.y; w = v.x }
var <T> Vec4T<T>.xwyy get() = Vec4T(x, w, y, y); set(v) { x = v.x; y = v.w; z = v.y; w = v.y }
var <T> Vec4T<T>.xwyz get() = Vec4T(x, w, y, z); set(v) { x = v.x; y = v.w; z = v.y; w = v.z }
var <T> Vec4T<T>.xwyw get() = Vec4T(x, w, y, w); set(v) { x = v.x; y = v.w; z = v.y; w = v.w }
var <T> Vec4T<T>.xwzx get() = Vec4T(x, w, z, x); set(v) { x = v.x; y = v.w; z = v.z; w = v.x }
var <T> Vec4T<T>.xwzy get() = Vec4T(x, w, z, y); set(v) { x = v.x; y = v.w; z = v.z; w = v.y }
var <T> Vec4T<T>.xwzz get() = Vec4T(x, w, z, z); set(v) { x = v.x; y = v.w; z = v.z; w = v.z }
var <T> Vec4T<T>.xwzw get() = Vec4T(x, w, z, w); set(v) { x = v.x; y = v.w; z = v.z; w = v.w }
var <T> Vec4T<T>.xwwx get() = Vec4T(x, w, w, x); set(v) { x = v.x; y = v.w; z = v.w; w = v.x }
var <T> Vec4T<T>.xwwy get() = Vec4T(x, w, w, y); set(v) { x = v.x; y = v.w; z = v.w; w = v.y }
var <T> Vec4T<T>.xwwz get() = Vec4T(x, w, w, z); set(v) { x = v.x; y = v.w; z = v.w; w = v.z }
var <T> Vec4T<T>.xwww get() = Vec4T(x, w, w, w); set(v) { x = v.x; y = v.w; z = v.w; w = v.w }
var <T> Vec4T<T>.yxxx get() = Vec4T(y, x, x, x); set(v) { x = v.y; y = v.x; z = v.x; w = v.x }
var <T> Vec4T<T>.yxxy get() = Vec4T(y, x, x, y); set(v) { x = v.y; y = v.x; z = v.x; w = v.y }
var <T> Vec4T<T>.yxxz get() = Vec4T(y, x, x, z); set(v) { x = v.y; y = v.x; z = v.x; w = v.z }
var <T> Vec4T<T>.yxxw get() = Vec4T(y, x, x, w); set(v) { x = v.y; y = v.x; z = v.x; w = v.w }
var <T> Vec4T<T>.yxyx get() = Vec4T(y, x, y, x); set(v) { x = v.y; y = v.x; z = v.y; w = v.x }
var <T> Vec4T<T>.yxyy get() = Vec4T(y, x, y, y); set(v) { x = v.y; y = v.x; z = v.y; w = v.y }
var <T> Vec4T<T>.yxyz get() = Vec4T(y, x, y, z); set(v) { x = v.y; y = v.x; z = v.y; w = v.z }
var <T> Vec4T<T>.yxyw get() = Vec4T(y, x, y, w); set(v) { x = v.y; y = v.x; z = v.y; w = v.w }
var <T> Vec4T<T>.yxzx get() = Vec4T(y, x, z, x); set(v) { x = v.y; y = v.x; z = v.z; w = v.x }
var <T> Vec4T<T>.yxzy get() = Vec4T(y, x, z, y); set(v) { x = v.y; y = v.x; z = v.z; w = v.y }
var <T> Vec4T<T>.yxzz get() = Vec4T(y, x, z, z); set(v) { x = v.y; y = v.x; z = v.z; w = v.z }
var <T> Vec4T<T>.yxzw get() = Vec4T(y, x, z, w); set(v) { x = v.y; y = v.x; z = v.z; w = v.w }
var <T> Vec4T<T>.yxwx get() = Vec4T(y, x, w, x); set(v) { x = v.y; y = v.x; z = v.w; w = v.x }
var <T> Vec4T<T>.yxwy get() = Vec4T(y, x, w, y); set(v) { x = v.y; y = v.x; z = v.w; w = v.y }
var <T> Vec4T<T>.yxwz get() = Vec4T(y, x, w, z); set(v) { x = v.y; y = v.x; z = v.w; w = v.z }
var <T> Vec4T<T>.yxww get() = Vec4T(y, x, w, w); set(v) { x = v.y; y = v.x; z = v.w; w = v.w }
var <T> Vec4T<T>.yyxx get() = Vec4T(y, y, x, x); set(v) { x = v.y; y = v.y; z = v.x; w = v.x }
var <T> Vec4T<T>.yyxy get() = Vec4T(y, y, x, y); set(v) { x = v.y; y = v.y; z = v.x; w = v.y }
var <T> Vec4T<T>.yyxz get() = Vec4T(y, y, x, z); set(v) { x = v.y; y = v.y; z = v.x; w = v.z }
var <T> Vec4T<T>.yyxw get() = Vec4T(y, y, x, w); set(v) { x = v.y; y = v.y; z = v.x; w = v.w }
var <T> Vec4T<T>.yyyx get() = Vec4T(y, y, y, x); set(v) { x = v.y; y = v.y; z = v.y; w = v.x }
var <T> Vec4T<T>.yyyy get() = Vec4T(y, y, y, y); set(v) { x = v.y; y = v.y; z = v.y; w = v.y }
var <T> Vec4T<T>.yyyz get() = Vec4T(y, y, y, z); set(v) { x = v.y; y = v.y; z = v.y; w = v.z }
var <T> Vec4T<T>.yyyw get() = Vec4T(y, y, y, w); set(v) { x = v.y; y = v.y; z = v.y; w = v.w }
var <T> Vec4T<T>.yyzx get() = Vec4T(y, y, z, x); set(v) { x = v.y; y = v.y; z = v.z; w = v.x }
var <T> Vec4T<T>.yyzy get() = Vec4T(y, y, z, y); set(v) { x = v.y; y = v.y; z = v.z; w = v.y }
var <T> Vec4T<T>.yyzz get() = Vec4T(y, y, z, z); set(v) { x = v.y; y = v.y; z = v.z; w = v.z }
var <T> Vec4T<T>.yyzw get() = Vec4T(y, y, z, w); set(v) { x = v.y; y = v.y; z = v.z; w = v.w }
var <T> Vec4T<T>.yywx get() = Vec4T(y, y, w, x); set(v) { x = v.y; y = v.y; z = v.w; w = v.x }
var <T> Vec4T<T>.yywy get() = Vec4T(y, y, w, y); set(v) { x = v.y; y = v.y; z = v.w; w = v.y }
var <T> Vec4T<T>.yywz get() = Vec4T(y, y, w, z); set(v) { x = v.y; y = v.y; z = v.w; w = v.z }
var <T> Vec4T<T>.yyww get() = Vec4T(y, y, w, w); set(v) { x = v.y; y = v.y; z = v.w; w = v.w }
var <T> Vec4T<T>.yzxx get() = Vec4T(y, z, x, x); set(v) { x = v.y; y = v.z; z = v.x; w = v.x }
var <T> Vec4T<T>.yzxy get() = Vec4T(y, z, x, y); set(v) { x = v.y; y = v.z; z = v.x; w = v.y }
var <T> Vec4T<T>.yzxz get() = Vec4T(y, z, x, z); set(v) { x = v.y; y = v.z; z = v.x; w = v.z }
var <T> Vec4T<T>.yzxw get() = Vec4T(y, z, x, w); set(v) { x = v.y; y = v.z; z = v.x; w = v.w }
var <T> Vec4T<T>.yzyx get() = Vec4T(y, z, y, x); set(v) { x = v.y; y = v.z; z = v.y; w = v.x }
var <T> Vec4T<T>.yzyy get() = Vec4T(y, z, y, y); set(v) { x = v.y; y = v.z; z = v.y; w = v.y }
var <T> Vec4T<T>.yzyz get() = Vec4T(y, z, y, z); set(v) { x = v.y; y = v.z; z = v.y; w = v.z }
var <T> Vec4T<T>.yzyw get() = Vec4T(y, z, y, w); set(v) { x = v.y; y = v.z; z = v.y; w = v.w }
var <T> Vec4T<T>.yzzx get() = Vec4T(y, z, z, x); set(v) { x = v.y; y = v.z; z = v.z; w = v.x }
var <T> Vec4T<T>.yzzy get() = Vec4T(y, z, z, y); set(v) { x = v.y; y = v.z; z = v.z; w = v.y }
var <T> Vec4T<T>.yzzz get() = Vec4T(y, z, z, z); set(v) { x = v.y; y = v.z; z = v.z; w = v.z }
var <T> Vec4T<T>.yzzw get() = Vec4T(y, z, z, w); set(v) { x = v.y; y = v.z; z = v.z; w = v.w }
var <T> Vec4T<T>.yzwx get() = Vec4T(y, z, w, x); set(v) { x = v.y; y = v.z; z = v.w; w = v.x }
var <T> Vec4T<T>.yzwy get() = Vec4T(y, z, w, y); set(v) { x = v.y; y = v.z; z = v.w; w = v.y }
var <T> Vec4T<T>.yzwz get() = Vec4T(y, z, w, z); set(v) { x = v.y; y = v.z; z = v.w; w = v.z }
var <T> Vec4T<T>.yzww get() = Vec4T(y, z, w, w); set(v) { x = v.y; y = v.z; z = v.w; w = v.w }
var <T> Vec4T<T>.ywxx get() = Vec4T(y, w, x, x); set(v) { x = v.y; y = v.w; z = v.x; w = v.x }
var <T> Vec4T<T>.ywxy get() = Vec4T(y, w, x, y); set(v) { x = v.y; y = v.w; z = v.x; w = v.y }
var <T> Vec4T<T>.ywxz get() = Vec4T(y, w, x, z); set(v) { x = v.y; y = v.w; z = v.x; w = v.z }
var <T> Vec4T<T>.ywxw get() = Vec4T(y, w, x, w); set(v) { x = v.y; y = v.w; z = v.x; w = v.w }
var <T> Vec4T<T>.ywyx get() = Vec4T(y, w, y, x); set(v) { x = v.y; y = v.w; z = v.y; w = v.x }
var <T> Vec4T<T>.ywyy get() = Vec4T(y, w, y, y); set(v) { x = v.y; y = v.w; z = v.y; w = v.y }
var <T> Vec4T<T>.ywyz get() = Vec4T(y, w, y, z); set(v) { x = v.y; y = v.w; z = v.y; w = v.z }
var <T> Vec4T<T>.ywyw get() = Vec4T(y, w, y, w); set(v) { x = v.y; y = v.w; z = v.y; w = v.w }
var <T> Vec4T<T>.ywzx get() = Vec4T(y, w, z, x); set(v) { x = v.y; y = v.w; z = v.z; w = v.x }
var <T> Vec4T<T>.ywzy get() = Vec4T(y, w, z, y); set(v) { x = v.y; y = v.w; z = v.z; w = v.y }
var <T> Vec4T<T>.ywzz get() = Vec4T(y, w, z, z); set(v) { x = v.y; y = v.w; z = v.z; w = v.z }
var <T> Vec4T<T>.ywzw get() = Vec4T(y, w, z, w); set(v) { x = v.y; y = v.w; z = v.z; w = v.w }
var <T> Vec4T<T>.ywwx get() = Vec4T(y, w, w, x); set(v) { x = v.y; y = v.w; z = v.w; w = v.x }
var <T> Vec4T<T>.ywwy get() = Vec4T(y, w, w, y); set(v) { x = v.y; y = v.w; z = v.w; w = v.y }
var <T> Vec4T<T>.ywwz get() = Vec4T(y, w, w, z); set(v) { x = v.y; y = v.w; z = v.w; w = v.z }
var <T> Vec4T<T>.ywww get() = Vec4T(y, w, w, w); set(v) { x = v.y; y = v.w; z = v.w; w = v.w }
var <T> Vec4T<T>.zxxx get() = Vec4T(z, x, x, x); set(v) { x = v.z; y = v.x; z = v.x; w = v.x }
var <T> Vec4T<T>.zxxy get() = Vec4T(z, x, x, y); set(v) { x = v.z; y = v.x; z = v.x; w = v.y }
var <T> Vec4T<T>.zxxz get() = Vec4T(z, x, x, z); set(v) { x = v.z; y = v.x; z = v.x; w = v.z }
var <T> Vec4T<T>.zxxw get() = Vec4T(z, x, x, w); set(v) { x = v.z; y = v.x; z = v.x; w = v.w }
var <T> Vec4T<T>.zxyx get() = Vec4T(z, x, y, x); set(v) { x = v.z; y = v.x; z = v.y; w = v.x }
var <T> Vec4T<T>.zxyy get() = Vec4T(z, x, y, y); set(v) { x = v.z; y = v.x; z = v.y; w = v.y }
var <T> Vec4T<T>.zxyz get() = Vec4T(z, x, y, z); set(v) { x = v.z; y = v.x; z = v.y; w = v.z }
var <T> Vec4T<T>.zxyw get() = Vec4T(z, x, y, w); set(v) { x = v.z; y = v.x; z = v.y; w = v.w }
var <T> Vec4T<T>.zxzx get() = Vec4T(z, x, z, x); set(v) { x = v.z; y = v.x; z = v.z; w = v.x }
var <T> Vec4T<T>.zxzy get() = Vec4T(z, x, z, y); set(v) { x = v.z; y = v.x; z = v.z; w = v.y }
var <T> Vec4T<T>.zxzz get() = Vec4T(z, x, z, z); set(v) { x = v.z; y = v.x; z = v.z; w = v.z }
var <T> Vec4T<T>.zxzw get() = Vec4T(z, x, z, w); set(v) { x = v.z; y = v.x; z = v.z; w = v.w }
var <T> Vec4T<T>.zxwx get() = Vec4T(z, x, w, x); set(v) { x = v.z; y = v.x; z = v.w; w = v.x }
var <T> Vec4T<T>.zxwy get() = Vec4T(z, x, w, y); set(v) { x = v.z; y = v.x; z = v.w; w = v.y }
var <T> Vec4T<T>.zxwz get() = Vec4T(z, x, w, z); set(v) { x = v.z; y = v.x; z = v.w; w = v.z }
var <T> Vec4T<T>.zxww get() = Vec4T(z, x, w, w); set(v) { x = v.z; y = v.x; z = v.w; w = v.w }
var <T> Vec4T<T>.zyxx get() = Vec4T(z, y, x, x); set(v) { x = v.z; y = v.y; z = v.x; w = v.x }
var <T> Vec4T<T>.zyxy get() = Vec4T(z, y, x, y); set(v) { x = v.z; y = v.y; z = v.x; w = v.y }
var <T> Vec4T<T>.zyxz get() = Vec4T(z, y, x, z); set(v) { x = v.z; y = v.y; z = v.x; w = v.z }
var <T> Vec4T<T>.zyxw get() = Vec4T(z, y, x, w); set(v) { x = v.z; y = v.y; z = v.x; w = v.w }
var <T> Vec4T<T>.zyyx get() = Vec4T(z, y, y, x); set(v) { x = v.z; y = v.y; z = v.y; w = v.x }
var <T> Vec4T<T>.zyyy get() = Vec4T(z, y, y, y); set(v) { x = v.z; y = v.y; z = v.y; w = v.y }
var <T> Vec4T<T>.zyyz get() = Vec4T(z, y, y, z); set(v) { x = v.z; y = v.y; z = v.y; w = v.z }
var <T> Vec4T<T>.zyyw get() = Vec4T(z, y, y, w); set(v) { x = v.z; y = v.y; z = v.y; w = v.w }
var <T> Vec4T<T>.zyzx get() = Vec4T(z, y, z, x); set(v) { x = v.z; y = v.y; z = v.z; w = v.x }
var <T> Vec4T<T>.zyzy get() = Vec4T(z, y, z, y); set(v) { x = v.z; y = v.y; z = v.z; w = v.y }
var <T> Vec4T<T>.zyzz get() = Vec4T(z, y, z, z); set(v) { x = v.z; y = v.y; z = v.z; w = v.z }
var <T> Vec4T<T>.zyzw get() = Vec4T(z, y, z, w); set(v) { x = v.z; y = v.y; z = v.z; w = v.w }
var <T> Vec4T<T>.zywx get() = Vec4T(z, y, w, x); set(v) { x = v.z; y = v.y; z = v.w; w = v.x }
var <T> Vec4T<T>.zywy get() = Vec4T(z, y, w, y); set(v) { x = v.z; y = v.y; z = v.w; w = v.y }
var <T> Vec4T<T>.zywz get() = Vec4T(z, y, w, z); set(v) { x = v.z; y = v.y; z = v.w; w = v.z }
var <T> Vec4T<T>.zyww get() = Vec4T(z, y, w, w); set(v) { x = v.z; y = v.y; z = v.w; w = v.w }
var <T> Vec4T<T>.zzxx get() = Vec4T(z, z, x, x); set(v) { x = v.z; y = v.z; z = v.x; w = v.x }
var <T> Vec4T<T>.zzxy get() = Vec4T(z, z, x, y); set(v) { x = v.z; y = v.z; z = v.x; w = v.y }
var <T> Vec4T<T>.zzxz get() = Vec4T(z, z, x, z); set(v) { x = v.z; y = v.z; z = v.x; w = v.z }
var <T> Vec4T<T>.zzxw get() = Vec4T(z, z, x, w); set(v) { x = v.z; y = v.z; z = v.x; w = v.w }
var <T> Vec4T<T>.zzyx get() = Vec4T(z, z, y, x); set(v) { x = v.z; y = v.z; z = v.y; w = v.x }
var <T> Vec4T<T>.zzyy get() = Vec4T(z, z, y, y); set(v) { x = v.z; y = v.z; z = v.y; w = v.y }
var <T> Vec4T<T>.zzyz get() = Vec4T(z, z, y, z); set(v) { x = v.z; y = v.z; z = v.y; w = v.z }
var <T> Vec4T<T>.zzyw get() = Vec4T(z, z, y, w); set(v) { x = v.z; y = v.z; z = v.y; w = v.w }
var <T> Vec4T<T>.zzzx get() = Vec4T(z, z, z, x); set(v) { x = v.z; y = v.z; z = v.z; w = v.x }
var <T> Vec4T<T>.zzzy get() = Vec4T(z, z, z, y); set(v) { x = v.z; y = v.z; z = v.z; w = v.y }
var <T> Vec4T<T>.zzzz get() = Vec4T(z, z, z, z); set(v) { x = v.z; y = v.z; z = v.z; w = v.z }
var <T> Vec4T<T>.zzzw get() = Vec4T(z, z, z, w); set(v) { x = v.z; y = v.z; z = v.z; w = v.w }
var <T> Vec4T<T>.zzwx get() = Vec4T(z, z, w, x); set(v) { x = v.z; y = v.z; z = v.w; w = v.x }
var <T> Vec4T<T>.zzwy get() = Vec4T(z, z, w, y); set(v) { x = v.z; y = v.z; z = v.w; w = v.y }
var <T> Vec4T<T>.zzwz get() = Vec4T(z, z, w, z); set(v) { x = v.z; y = v.z; z = v.w; w = v.z }
var <T> Vec4T<T>.zzww get() = Vec4T(z, z, w, w); set(v) { x = v.z; y = v.z; z = v.w; w = v.w }
var <T> Vec4T<T>.zwxx get() = Vec4T(z, w, x, x); set(v) { x = v.z; y = v.w; z = v.x; w = v.x }
var <T> Vec4T<T>.zwxy get() = Vec4T(z, w, x, y); set(v) { x = v.z; y = v.w; z = v.x; w = v.y }
var <T> Vec4T<T>.zwxz get() = Vec4T(z, w, x, z); set(v) { x = v.z; y = v.w; z = v.x; w = v.z }
var <T> Vec4T<T>.zwxw get() = Vec4T(z, w, x, w); set(v) { x = v.z; y = v.w; z = v.x; w = v.w }
var <T> Vec4T<T>.zwyx get() = Vec4T(z, w, y, x); set(v) { x = v.z; y = v.w; z = v.y; w = v.x }
var <T> Vec4T<T>.zwyy get() = Vec4T(z, w, y, y); set(v) { x = v.z; y = v.w; z = v.y; w = v.y }
var <T> Vec4T<T>.zwyz get() = Vec4T(z, w, y, z); set(v) { x = v.z; y = v.w; z = v.y; w = v.z }
var <T> Vec4T<T>.zwyw get() = Vec4T(z, w, y, w); set(v) { x = v.z; y = v.w; z = v.y; w = v.w }
var <T> Vec4T<T>.zwzx get() = Vec4T(z, w, z, x); set(v) { x = v.z; y = v.w; z = v.z; w = v.x }
var <T> Vec4T<T>.zwzy get() = Vec4T(z, w, z, y); set(v) { x = v.z; y = v.w; z = v.z; w = v.y }
var <T> Vec4T<T>.zwzz get() = Vec4T(z, w, z, z); set(v) { x = v.z; y = v.w; z = v.z; w = v.z }
var <T> Vec4T<T>.zwzw get() = Vec4T(z, w, z, w); set(v) { x = v.z; y = v.w; z = v.z; w = v.w }
var <T> Vec4T<T>.zwwx get() = Vec4T(z, w, w, x); set(v) { x = v.z; y = v.w; z = v.w; w = v.x }
var <T> Vec4T<T>.zwwy get() = Vec4T(z, w, w, y); set(v) { x = v.z; y = v.w; z = v.w; w = v.y }
var <T> Vec4T<T>.zwwz get() = Vec4T(z, w, w, z); set(v) { x = v.z; y = v.w; z = v.w; w = v.z }
var <T> Vec4T<T>.zwww get() = Vec4T(z, w, w, w); set(v) { x = v.z; y = v.w; z = v.w; w = v.w }
var <T> Vec4T<T>.wxxx get() = Vec4T(w, x, x, x); set(v) { x = v.w; y = v.x; z = v.x; w = v.x }
var <T> Vec4T<T>.wxxy get() = Vec4T(w, x, x, y); set(v) { x = v.w; y = v.x; z = v.x; w = v.y }
var <T> Vec4T<T>.wxxz get() = Vec4T(w, x, x, z); set(v) { x = v.w; y = v.x; z = v.x; w = v.z }
var <T> Vec4T<T>.wxxw get() = Vec4T(w, x, x, w); set(v) { x = v.w; y = v.x; z = v.x; w = v.w }
var <T> Vec4T<T>.wxyx get() = Vec4T(w, x, y, x); set(v) { x = v.w; y = v.x; z = v.y; w = v.x }
var <T> Vec4T<T>.wxyy get() = Vec4T(w, x, y, y); set(v) { x = v.w; y = v.x; z = v.y; w = v.y }
var <T> Vec4T<T>.wxyz get() = Vec4T(w, x, y, z); set(v) { x = v.w; y = v.x; z = v.y; w = v.z }
var <T> Vec4T<T>.wxyw get() = Vec4T(w, x, y, w); set(v) { x = v.w; y = v.x; z = v.y; w = v.w }
var <T> Vec4T<T>.wxzx get() = Vec4T(w, x, z, x); set(v) { x = v.w; y = v.x; z = v.z; w = v.x }
var <T> Vec4T<T>.wxzy get() = Vec4T(w, x, z, y); set(v) { x = v.w; y = v.x; z = v.z; w = v.y }
var <T> Vec4T<T>.wxzz get() = Vec4T(w, x, z, z); set(v) { x = v.w; y = v.x; z = v.z; w = v.z }
var <T> Vec4T<T>.wxzw get() = Vec4T(w, x, z, w); set(v) { x = v.w; y = v.x; z = v.z; w = v.w }
var <T> Vec4T<T>.wxwx get() = Vec4T(w, x, w, x); set(v) { x = v.w; y = v.x; z = v.w; w = v.x }
var <T> Vec4T<T>.wxwy get() = Vec4T(w, x, w, y); set(v) { x = v.w; y = v.x; z = v.w; w = v.y }
var <T> Vec4T<T>.wxwz get() = Vec4T(w, x, w, z); set(v) { x = v.w; y = v.x; z = v.w; w = v.z }
var <T> Vec4T<T>.wxww get() = Vec4T(w, x, w, w); set(v) { x = v.w; y = v.x; z = v.w; w = v.w }
var <T> Vec4T<T>.wyxx get() = Vec4T(w, y, x, x); set(v) { x = v.w; y = v.y; z = v.x; w = v.x }
var <T> Vec4T<T>.wyxy get() = Vec4T(w, y, x, y); set(v) { x = v.w; y = v.y; z = v.x; w = v.y }
var <T> Vec4T<T>.wyxz get() = Vec4T(w, y, x, z); set(v) { x = v.w; y = v.y; z = v.x; w = v.z }
var <T> Vec4T<T>.wyxw get() = Vec4T(w, y, x, w); set(v) { x = v.w; y = v.y; z = v.x; w = v.w }
var <T> Vec4T<T>.wyyx get() = Vec4T(w, y, y, x); set(v) { x = v.w; y = v.y; z = v.y; w = v.x }
var <T> Vec4T<T>.wyyy get() = Vec4T(w, y, y, y); set(v) { x = v.w; y = v.y; z = v.y; w = v.y }
var <T> Vec4T<T>.wyyz get() = Vec4T(w, y, y, z); set(v) { x = v.w; y = v.y; z = v.y; w = v.z }
var <T> Vec4T<T>.wyyw get() = Vec4T(w, y, y, w); set(v) { x = v.w; y = v.y; z = v.y; w = v.w }
var <T> Vec4T<T>.wyzx get() = Vec4T(w, y, z, x); set(v) { x = v.w; y = v.y; z = v.z; w = v.x }
var <T> Vec4T<T>.wyzy get() = Vec4T(w, y, z, y); set(v) { x = v.w; y = v.y; z = v.z; w = v.y }
var <T> Vec4T<T>.wyzz get() = Vec4T(w, y, z, z); set(v) { x = v.w; y = v.y; z = v.z; w = v.z }
var <T> Vec4T<T>.wyzw get() = Vec4T(w, y, z, w); set(v) { x = v.w; y = v.y; z = v.z; w = v.w }
var <T> Vec4T<T>.wywx get() = Vec4T(w, y, w, x); set(v) { x = v.w; y = v.y; z = v.w; w = v.x }
var <T> Vec4T<T>.wywy get() = Vec4T(w, y, w, y); set(v) { x = v.w; y = v.y; z = v.w; w = v.y }
var <T> Vec4T<T>.wywz get() = Vec4T(w, y, w, z); set(v) { x = v.w; y = v.y; z = v.w; w = v.z }
var <T> Vec4T<T>.wyww get() = Vec4T(w, y, w, w); set(v) { x = v.w; y = v.y; z = v.w; w = v.w }
var <T> Vec4T<T>.wzxx get() = Vec4T(w, z, x, x); set(v) { x = v.w; y = v.z; z = v.x; w = v.x }
var <T> Vec4T<T>.wzxy get() = Vec4T(w, z, x, y); set(v) { x = v.w; y = v.z; z = v.x; w = v.y }
var <T> Vec4T<T>.wzxz get() = Vec4T(w, z, x, z); set(v) { x = v.w; y = v.z; z = v.x; w = v.z }
var <T> Vec4T<T>.wzxw get() = Vec4T(w, z, x, w); set(v) { x = v.w; y = v.z; z = v.x; w = v.w }
var <T> Vec4T<T>.wzyx get() = Vec4T(w, z, y, x); set(v) { x = v.w; y = v.z; z = v.y; w = v.x }
var <T> Vec4T<T>.wzyy get() = Vec4T(w, z, y, y); set(v) { x = v.w; y = v.z; z = v.y; w = v.y }
var <T> Vec4T<T>.wzyz get() = Vec4T(w, z, y, z); set(v) { x = v.w; y = v.z; z = v.y; w = v.z }
var <T> Vec4T<T>.wzyw get() = Vec4T(w, z, y, w); set(v) { x = v.w; y = v.z; z = v.y; w = v.w }
var <T> Vec4T<T>.wzzx get() = Vec4T(w, z, z, x); set(v) { x = v.w; y = v.z; z = v.z; w = v.x }
var <T> Vec4T<T>.wzzy get() = Vec4T(w, z, z, y); set(v) { x = v.w; y = v.z; z = v.z; w = v.y }
var <T> Vec4T<T>.wzzz get() = Vec4T(w, z, z, z); set(v) { x = v.w; y = v.z; z = v.z; w = v.z }
var <T> Vec4T<T>.wzzw get() = Vec4T(w, z, z, w); set(v) { x = v.w; y = v.z; z = v.z; w = v.w }
var <T> Vec4T<T>.wzwx get() = Vec4T(w, z, w, x); set(v) { x = v.w; y = v.z; z = v.w; w = v.x }
var <T> Vec4T<T>.wzwy get() = Vec4T(w, z, w, y); set(v) { x = v.w; y = v.z; z = v.w; w = v.y }
var <T> Vec4T<T>.wzwz get() = Vec4T(w, z, w, z); set(v) { x = v.w; y = v.z; z = v.w; w = v.z }
var <T> Vec4T<T>.wzww get() = Vec4T(w, z, w, w); set(v) { x = v.w; y = v.z; z = v.w; w = v.w }
var <T> Vec4T<T>.wwxx get() = Vec4T(w, w, x, x); set(v) { x = v.w; y = v.w; z = v.x; w = v.x }
var <T> Vec4T<T>.wwxy get() = Vec4T(w, w, x, y); set(v) { x = v.w; y = v.w; z = v.x; w = v.y }
var <T> Vec4T<T>.wwxz get() = Vec4T(w, w, x, z); set(v) { x = v.w; y = v.w; z = v.x; w = v.z }
var <T> Vec4T<T>.wwxw get() = Vec4T(w, w, x, w); set(v) { x = v.w; y = v.w; z = v.x; w = v.w }
var <T> Vec4T<T>.wwyx get() = Vec4T(w, w, y, x); set(v) { x = v.w; y = v.w; z = v.y; w = v.x }
var <T> Vec4T<T>.wwyy get() = Vec4T(w, w, y, y); set(v) { x = v.w; y = v.w; z = v.y; w = v.y }
var <T> Vec4T<T>.wwyz get() = Vec4T(w, w, y, z); set(v) { x = v.w; y = v.w; z = v.y; w = v.z }
var <T> Vec4T<T>.wwyw get() = Vec4T(w, w, y, w); set(v) { x = v.w; y = v.w; z = v.y; w = v.w }
var <T> Vec4T<T>.wwzx get() = Vec4T(w, w, z, x); set(v) { x = v.w; y = v.w; z = v.z; w = v.x }
var <T> Vec4T<T>.wwzy get() = Vec4T(w, w, z, y); set(v) { x = v.w; y = v.w; z = v.z; w = v.y }
var <T> Vec4T<T>.wwzz get() = Vec4T(w, w, z, z); set(v) { x = v.w; y = v.w; z = v.z; w = v.z }
var <T> Vec4T<T>.wwzw get() = Vec4T(w, w, z, w); set(v) { x = v.w; y = v.w; z = v.z; w = v.w }
var <T> Vec4T<T>.wwwx get() = Vec4T(w, w, w, x); set(v) { x = v.w; y = v.w; z = v.w; w = v.x }
var <T> Vec4T<T>.wwwy get() = Vec4T(w, w, w, y); set(v) { x = v.w; y = v.w; z = v.w; w = v.y }
var <T> Vec4T<T>.wwwz get() = Vec4T(w, w, w, z); set(v) { x = v.w; y = v.w; z = v.w; w = v.z }
var <T> Vec4T<T>.wwww get() = Vec4T(w, w, w, w); set(v) { x = v.w; y = v.w; z = v.w; w = v.w }