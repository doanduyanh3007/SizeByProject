export function getStarType(rating, index) {
  const value = Number(rating);
  if (!Number.isFinite(value) || value <= 0) {
    return "empty";
  }
  if (value >= index) {
    return "full";
  }
  if (value >= index - 0.5) {
    return "half";
  }
  return "empty";
}

export function getStarIconName(rating, index) {
  return getStarType(rating, index) === "half" ? "star_half" : "star";
}

export function isStarFilled(rating, index) {
  const type = getStarType(rating, index);
  return type === "full" || type === "half";
}
