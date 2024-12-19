import { Avatar } from "@mui/material";

function AvatarBox(props) {
  return (
    <div className="avatar-box">
      <Avatar>
        {props.userName[0]}
      </Avatar>
      <p className="avatar-label">
        {props.userName}  
      </p>
    </div>
  );
}

export default AvatarBox;